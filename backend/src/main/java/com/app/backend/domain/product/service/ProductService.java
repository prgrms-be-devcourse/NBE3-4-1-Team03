package com.app.backend.domain.product.service;

import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.global.error.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private static final String CREATED_DATE = "created_date";
    private static final String PRICE = "price";
    private static final String NAME = "name";

    public Page<Product> findBySortedPaged(
            int page, int size, String sort) {
        // TODO : 오름차순 / 내림차순 구분 있어야 할것 같음.(Sort 사용한 구현 가능!!)
        Pageable pageable = PageRequest.of(page,size);
        switch (sort) {
            case CREATED_DATE:
                return productRepository.findByOrderByCreatedDateDesc(pageable);
            case PRICE:
                return productRepository.findByOrderByPriceDesc(pageable);
            case NAME:
                return productRepository.findByOrderByNameDesc(pageable);
            default:
                throw new ProductException(ErrorCode.PRODUCT_SORT_NOT_EXISTS);
        }
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }
}
