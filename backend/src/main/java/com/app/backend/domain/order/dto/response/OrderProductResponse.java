package com.app.backend.domain.order.dto.response;

import com.app.backend.domain.order.entity.OrderProduct;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * PackageName : com.app.backend.domain.order.dto.response
 * FileName    : OrderProductResponse
 * Author      : loadingKKamo21
 * Date        : 25. 1. 15.
 * Description :
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderProductResponse {

    private final String     name;
    private final BigDecimal price;
    private final int        amount;
    private final BigDecimal totalPrice;

    private OrderProductResponse(final OrderProduct orderProduct) {
        this.name = orderProduct.getProduct().getName();
        this.price = orderProduct.getProductPrice();
        this.amount = orderProduct.getProductAmount();
        this.totalPrice = orderProduct.getTotalProductPrice();
    }

    public static OrderProductResponse of(final OrderProduct orderProduct) {
        return new OrderProductResponse(orderProduct);
    }

}
