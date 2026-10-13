package com.exam.productserver.controller;

import com.exam.productserver.dto.request.UpdateProductRequest;
import com.exam.productserver.entity.Product;
import com.exam.productserver.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        return productService.updateProductById(id, request);
    }

    @PostMapping("/{id}")
    public Product buyProduct(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity) {
        return productService.buyProduct(id, quantity);
    }
}
