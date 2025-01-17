package com.app.backend.domain.product.service;

import com.app.backend.domain.product.controller.ApiV1ProductController;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.repository.ProductRepository;
import com.app.backend.global.error.exception.ErrorCode;
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

    public boolean checkStockAvailable(Long product_id, Integer stock){
        Product product = this.findById(product_id);
        return product.getStock() >= stock;
    }

    public void updateStockAfterPayment(Long product_id, Integer stock){
        Product product = this.findById(product_id);
        product.setStock(product.getStock()-stock);
    }

    // TODO : 캐싱을 활용해 주문이 가능할경우 미리 재고량을 빼두고,
    //  결제 성공시 캐시데이터 삭제, 실패시 재고량 원상복구 할 예정
}
