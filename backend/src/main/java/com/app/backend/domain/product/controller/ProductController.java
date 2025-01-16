package com.app.backend.domain.product.controller;

import com.app.backend.domain.product.dto.ProductDetailDto;
import com.app.backend.domain.product.dto.ProductPageDto;
import com.app.backend.domain.product.dto.ProductWithoutDescriptionDto;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.exception.ProductException;
import com.app.backend.domain.product.service.ProductService;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Transactional(readOnly = true)
    public RsData<ProductPageDto<ProductWithoutDescriptionDto>> items(
            @Valid @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
            @Valid @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @Valid @RequestParam(name = "sort", defaultValue = "created_date") String sort,
            @Valid @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @Valid @RequestParam(name = "keyword", defaultValue= "") String keyword
    ){
        return new RsData<>(
                true,
                "200",
                "상품 페이지 전체조회",
                new ProductPageDto<>(
                productService.findBySortedPaged(page-1, size, sort, direction, keyword)
                        .map(ProductWithoutDescriptionDto::new)));
    }

    @GetMapping("/{product_id}")
    @Transactional(readOnly = true)
    public RsData<ProductDetailDto> item(
            @PathVariable @Valid Long product_id
    ){
        Optional<Product> opProduct = productService.findById(product_id);

        if(opProduct.isEmpty()){
            throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Product product = opProduct.get();

        return new RsData<>(true,
                "200",
                "%d번 상품 상세사항입니다.".formatted(product_id),
                new ProductDetailDto(product));
    }


}
