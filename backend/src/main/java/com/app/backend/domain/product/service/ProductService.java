package com.app.backend.domain.product.service;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product add(ApiV1ProductController.AddProductReqBody addProductReqBody) {
        Product product = Product
                .builder()
                .name(addProductReqBody.name())
                .description(addProductReqBody.description())
                .price(addProductReqBody.price())
                .stock(addProductReqBody.amount())
                .build();
        return this.productRepository.save(product);
    }

    public Optional<Product> findById(long id) {
        return this.productRepository.findById(id);
    }

    public void modify(Product product, ApiV1ProductController.ModifyProductReqBody modifyProductReqBody) {
        if (modifyProductReqBody.name() != null) {
            product.setName(modifyProductReqBody.name());
        }

        if (modifyProductReqBody.description() != null) {
            product.setDescription(modifyProductReqBody.description());
        }

        if (modifyProductReqBody.price() != null) {
            product.setPrice(modifyProductReqBody.price());
        }

        if (modifyProductReqBody.amount() != null) {
            product.setStock(modifyProductReqBody.amount());
        }

        if (modifyProductReqBody.status() != null) {
            product.setStatus(modifyProductReqBody.status());
        }
    }

    public void flush() {
        this.productRepository.flush();
    }
}
