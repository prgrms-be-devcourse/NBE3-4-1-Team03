package com.app.backend.domain.order.dto.response;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.standard.util.Ut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class AdminOrderResponse {

    private final String userName;
    private final String userEmail;
    private final String orderNumber;
    private final String orderStatus;
    private final BigDecimal totalPrice;
    private final String orderAddress;
    private final String createdDate;

    private AdminOrderResponse(final Order order) {

        this.userName = order.getCustomer().getName();
        this.userEmail = order.getCustomer().getEmail();
        this.orderNumber = order.getOrderNumber();
        this.orderStatus = order.getStatus().name();
        this.totalPrice = order.getTotalPrice();
        this.orderAddress = order.getAddress();
        this.createdDate = Ut.Str.localDateTimeToString(order.getCreatedDate());
    }

    public static AdminOrderResponse of(final Order order) {
        return new AdminOrderResponse(order);
    }
}
