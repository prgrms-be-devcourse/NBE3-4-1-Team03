package com.app.backend.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

/**
 * PackageName : com.app.backend.domain.order.dto.response
 * FileName    : OrderResponse
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description :
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderResponse {

//    private final long          orderId;
//    private final long          customerId;
//    private final String        orderNumber;
//    private final int           totalAmount;
//    private final BigDecimal    totalPrice;
//    private final String        orderAddress;
//    private final String        orderStatus;
//    private final LocalDateTime createdDate;
//    private final LocalDateTime modifiedDate;
//
//    private OrderResponse(final Order order) {
//        this.orderId = order.getId();
//        this.customerId = order.getCustomer().getId();
//        this.orderNumber = order.getOrderNumber();
//        this.totalAmount = order.getTotalAmount();
//        this.totalPrice = order.getTotalPrice();
//        this.orderAddress = order.getAddress();
//        this.orderStatus = order.getStatus().name();
//        this.createdDate = order.getCreatedDate();
//        this.modifiedDate = order.getModifiedDate();
//    }
//
//    public static OrderResponse from(final Order order) {
//        return new OrderResponse(order);
//    }

}
