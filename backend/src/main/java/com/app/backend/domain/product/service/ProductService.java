package com.app.backend.domain.product.service;

import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product add(String name, String description, BigDecimal price, int amount) {
        Product product = Product
                .builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(amount)
                .build();
        return this.productRepository.save(product);
    }
}
