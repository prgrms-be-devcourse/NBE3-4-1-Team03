package com.app.backend.domain.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * PackageName : com.app.backend.domain.order.dto.request
 * FileName    : OrderRequest
 * Author      : 강찬우
 * Date        : 25. 1. 15.
 * Description :
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @JsonProperty("product_info")
    @NonNull
    @Size(min = 1)
    @Valid
    private List<OrderProductRequest> productInfo;
}
