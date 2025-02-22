package com.app.backend.domain.product.controller;

import com.app.backend.domain.product.dto.ProductDetailDto;
import com.app.backend.domain.product.dto.ProductPageDto;
import com.app.backend.domain.product.dto.ProductWithoutDescriptionDto;
import com.app.backend.domain.product.entity.Product;
import com.app.backend.domain.product.service.ProductService;
import com.app.backend.global.rs.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
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
        Product product = productService.findById(product_id);

        return new RsData<>(true,
                "200",
                "%d번 상품 상세사항입니다.".formatted(product_id),
                new ProductDetailDto(product));
    }

    public record AddProductReqBody(
            @NotBlank
            String name,

            @NotBlank
            String description,

            @NotNull
            @DecimalMin(value = "100.00")
            BigDecimal price,

            @NotNull
            @Min(0)
            Integer amount,

            @NotNull
            Boolean status
    ){}

    @PostMapping
    @Transactional
    public RsData<Void> add(@RequestBody @Valid AddProductReqBody addProductReqBody) {

        Product product = productService.add(addProductReqBody);

        return new RsData<>(
                true,
                "201",
                "상품 '%s'이(가) 등록되었습니다.".formatted(product.getName())
        );
    }

    public record ModifyProductReqBody(
            String name,

            String description,

            @DecimalMin(value = "100.00")
            BigDecimal price,

            @Min(0)
            Integer amount,

            Boolean status
    ) {}

    @PatchMapping("/{id}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable long id,
            @RequestBody @Valid ModifyProductReqBody modifyProductReqBody
    ) {
        Product product = productService.findById(id);

        this.productService.modify(product, modifyProductReqBody);

        this.productService.flush();

        return new RsData<>(
                true,
                "200",
                "상품 '%s'이(가) 수정되었습니다.".formatted(product.getName())
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable long id
    ) {
        Product product = this.productService.findById(id);

        this.productService.delete(product);

        return new RsData<>(
                true,
                "200",
                "상품 '%s'이(가) 삭제되었습니다.".formatted(product.getName())
        );
    }
}