package com.app.backend.domain.order.dto.response;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.standard.util.Ut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/**
 * PackageName : com.app.backend.domain.order.dto.response
 * FileName    : OrderResponse
 * Author      : loadingKKamo21
 * Date        : 25. 1. 15.
 * Description :
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderResponse {

    private final String                     orderNumber;
    private final String                     name;
    private final int                        totalAmount;
    private final BigDecimal                 totalPrice;
    private final String                     orderAddress;
    private final String                     orderStatus;
    private final List<OrderProductResponse> orderList;
    private final String                     createdDate;

    private OrderResponse(final Order order) {
        this.orderNumber = order.getOrderNumber();
        this.name = order.getCustomer().getName();
        this.totalAmount = order.getTotalAmount();
        this.totalPrice = order.getTotalPrice();
        this.orderAddress = order.getAddress();
        this.orderStatus = order.getStatus().name();
        this.orderList = order.getOrderProducts().stream().map(OrderProductResponse::of).toList();
        this.createdDate = Ut.Str.localDateTimeToString(order.getCreatedDate());
    }

    public static OrderResponse of(final Order order) {
        return new OrderResponse(order);
    }

}
