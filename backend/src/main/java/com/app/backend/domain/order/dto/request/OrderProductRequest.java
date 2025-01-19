package com.app.backend.domain.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.app.backend.domain.order.dto.request
 * FileName    : OrderProductRequest
 * Author      : loadingKKamo21
 * Date        : 25. 1. 15.
 * Description :
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductRequest {
    @JsonProperty("product_id")
    @NotNull
    @Min(1)
    private Long    productId;
    @NotNull
    @Min(1)
    private Integer amount;
}
