package com.app.backend.domain.product.service;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.redis.repository.RedisRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisRepository redisRepository;

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
        return productRepository.findByNameContaining(keyword, pageable);
    }

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

    @Transactional
    public boolean checkStockAvailableAndCaching(Long user_id, Long product_id, Integer amount){
        Product product = this.findById(product_id);
        if(product.getStock() < amount) return false;

        product.setStock(product.getStock()-amount);
        // TODO : Key값 전달하기
        // TODO : 중복된 Key를 만드는 요청이 들어온다면?
        String redisKey = "order-%s_%s".formatted(user_id,product_id);
        String redisKeyForValue = "orderValue-%s_%s".formatted(user_id,product_id);
        redisRepository.save(redisKey,amount,4, TimeUnit.MINUTES);
        redisRepository.save(redisKeyForValue,amount,5, TimeUnit.MINUTES);
        return true;
    }

    @Transactional
    public void deleteCacheAfterPayment(String redisKey){
        redisRepository.delete(redisKey);
    }

    @Transactional
    public void restoreStock(Long productId, Integer amount) {
        Product product = this.findById(productId);
        product.setStock(product.getStock()+amount);
    }
}
