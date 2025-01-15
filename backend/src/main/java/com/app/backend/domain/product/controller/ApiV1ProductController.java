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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductService productService;

    record AddProductReqBody(
            @NotBlank
            String name,

            @NotBlank
            String description,

            @NotNull
            @DecimalMin(value = "100.00")
            BigDecimal price,

            @NotNull
            @Min(0)
            int amount
    ){}
}
