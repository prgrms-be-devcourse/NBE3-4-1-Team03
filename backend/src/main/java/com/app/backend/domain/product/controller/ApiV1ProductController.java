package com.app.backend.domain.product.controller;

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
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductService productService;

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
            Integer amount
    ){}

    @PostMapping
    @Transactional
    public RsData<Void> add(@RequestBody @Valid AddProductReqBody addProductReqBody) {

        Product product = productService.add(addProductReqBody);

        return new RsData<>(
                true,
                "201",
                "%d번 상품이 등록되었습니다.".formatted(product.getId())
        );
    }

    public record ModifyProductReqBody(
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
    ) {}

    @PatchMapping("{id}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable long id,
            @RequestBody @Valid ModifyProductReqBody modifyProductReqBody
    ) {
        Product product = productService.findById(id).get();

        this.productService.modify(product, modifyProductReqBody);

        this.productService.flush();

        return new RsData<>(
                true,
                "200",
                "%d번 상품이 수정되었습니다.".formatted(product.getId())
        );
    }
}