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

    private Sort makeSortOption(String sort, String direction){
        if(!(direction.equals("asc") || direction.equals("desc"))){
            throw new ProductException(ErrorCode.PRODUCT_DIRECTION_NOT_EXISTS);
        }
        Sort sortOption;
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        switch (sort) {
            case CREATED_DATE:
                sortOption = Sort.by(sortDirection, "createdDate");
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
        return sortOption;
    }

    public Page<Product> findBySortedPagedWithoutSearchKeyword(
            int page, int size, String sort, String direction) {
        Sort sortOption = makeSortOption(sort, direction);
        Pageable pageable = PageRequest.of(page, size, sortOption);
        return productRepository.findAll(pageable);
    }

    public Page<Product> findBySortedPaged(
            int page, int size, String sort, String direction, String keyword) {
        if(keyword.isBlank()) return findBySortedPagedWithoutSearchKeyword(page, size, sort, direction);
        Sort sortOption = makeSortOption(sort,direction);
        Pageable pageable = PageRequest.of(page, size, sortOption);
        keyword = "%" + keyword + "%";
        return productRepository.findByNameLike(keyword, pageable);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }
}
