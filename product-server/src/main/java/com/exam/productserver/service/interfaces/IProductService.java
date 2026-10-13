package com.exam.productserver.service.interfaces;

import com.exam.productserver.dto.request.UpdateProductRequest;
import com.exam.productserver.entity.Product;

import java.util.List;

public interface IProductService {
    Product getProductById(Long id);

    Product updateProductById(Long id, UpdateProductRequest request);

    List<Product> getAllProducts();

    Product buyProduct(Long id, int quantity);
}
