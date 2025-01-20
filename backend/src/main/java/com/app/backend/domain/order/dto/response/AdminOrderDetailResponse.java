package com.app.backend.domain.order.dto.response;

import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.Payment;
import com.app.backend.domain.order.entity.PaymentMethod;
import com.app.backend.domain.order.entity.PaymentStatus;
import com.app.backend.standard.util.Ut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class AdminOrderDetailResponse {

    private final String orderNumber;
    private final String orderStatus;
    private final String orderCreatedDate;
    private final BigDecimal totalPrice;
    private final String orderAddress;
    private final List<OrderProductResponse> orderList;

    private final String userName;
    private final String userEmail;
    private final String userPhone;

    private final PaymentMethod paymentMethod;
    private final PaymentStatus paymentStatus;
    private final String paymentDate;

    private AdminOrderDetailResponse(final Order order, final Payment payment) {
        this.orderNumber = order.getOrderNumber();
        this.userName = order.getCustomer().getName();
        this.userEmail = order.getCustomer().getEmail();
        this.userPhone = order.getCustomer().getPhone();
        this.orderStatus = order.getStatus().name();
        this.orderCreatedDate = Ut.Str.localDateTimeToString(order.getCreatedDate());
        this.totalPrice = order.getTotalPrice();
        this.orderAddress = order.getAddress();

        this.orderList = order.getOrderProducts().stream()
                .map(OrderProductResponse::of)
                .collect(Collectors.toList());

        this.paymentMethod = payment.getMethod();
        this.paymentStatus = payment.getStatus();
        this.paymentDate = Ut.Str.localDateTimeToString(payment.getCreatedDate());
    }

    public static AdminOrderDetailResponse of(final Order order, final Payment payment) {
        return new AdminOrderDetailResponse(order, payment);
    }
}
