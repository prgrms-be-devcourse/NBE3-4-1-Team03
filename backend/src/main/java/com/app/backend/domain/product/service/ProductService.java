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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private static final String CREATED_DATE = "created_date";
    private static final String PRICE = "price";
    private static final String NAME = "name";

    public Page<Product> findBySortedPaged(
            int page, int size, String sort, String direction) {
        Sort sortOption;
        // TODO : direction값 검증
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        switch (sort) {
            case CREATED_DATE:
                sortOption = Sort.by(sortDirection, "createDate");
                break;
            case PRICE:
                sortOption = Sort.by(sortDirection, "price");
                break;
            case NAME:
                sortOption = Sort.by(sortDirection, "name");
                break;
            default:
                throw new ProductException(ErrorCode.PRODUCT_SORT_NOT_EXISTS);
        }

        Pageable pageable = PageRequest.of(page, size, sortOption);
        return productRepository.findAll(pageable);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }
}
