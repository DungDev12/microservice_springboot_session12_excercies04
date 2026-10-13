package com.exam.productserver.service;

import com.exam.productserver.dto.request.UpdateProductRequest;
import com.exam.productserver.entity.Product;
import com.exam.productserver.repository.ProductRepository;
import com.exam.productserver.service.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;

    @Override
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("Đọc từ Database: " + id);
        return getById(id);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public Product updateProductById(Long id, UpdateProductRequest request) {
        Product product = getById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setStock(request.stock());

        return productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    private Product getById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy sản phẩm"));
    }


    @Override
    public Product buyProduct(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng mua phải lớn hơn 0"
            );
        }

        // Mỗi sản phẩm có một distributed lock riêng
        String lockKey = "lock:product:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        boolean locked = false;

        try {
            // Chờ tối đa 3 giây, giữ lock tối đa 5 giây
            locked = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (!locked) {
                throw new IllegalStateException(
                        "Sản phẩm đang được xử lý, vui lòng thử lại"
                );
            }

            // Chỉ kiểm tra và trừ kho sau khi đã lấy được lock
            Product product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy sản phẩm"));

            if (product.getStock() < quantity) {
                throw new IllegalStateException(
                        "Sản phẩm đã hết hàng"
                );
            }

            product.setStock(product.getStock() - quantity);

            return productRepository.save(product);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Bị gián đoạn khi chờ khóa", e
            );
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
