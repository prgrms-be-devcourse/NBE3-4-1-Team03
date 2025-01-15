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

    public Page<Product> findBySortedPaged(
            int page, int size, String sort) {
        // TODO : 오름차순 / 내림차순 구분 있어야 할것 같음.(Sort 사용한 구현 가능!!)
        Pageable pageable = PageRequest.of(page,size);
        switch (sort) {
            case "created_date":
                return productRepository.findByOrderByCreatedDateDesc(pageable);
            case "price":
                return productRepository.findByOrderByPriceDesc(pageable);
            case "name":
                return productRepository.findByOrderByNameDesc(pageable);
            default:
                throw new ProductException(ErrorCode.PRODUCT_SORT_NOT_EXISTS);
        }
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }
}
