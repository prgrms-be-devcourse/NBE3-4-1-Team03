package com.app.backend.domain.product.service;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .status(addProductReqBody.status())
                .build();
        return this.productRepository.save(product);
    }

    public Product findById(long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
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

    public void delete(Product product) {
        this.productRepository.delete(product);
    }
}
