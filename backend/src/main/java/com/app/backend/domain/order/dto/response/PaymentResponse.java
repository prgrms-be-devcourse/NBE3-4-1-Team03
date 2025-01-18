package com.app.backend.domain.order.dto.response;

import com.app.backend.domain.order.entity.Payment;
import com.app.backend.standard.util.Ut;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * PackageName : com.app.backend.domain.order.dto.response
 * FileName    : PaymentResponse
 * Author      : 강찬우
 * Date        : 25. 1. 18.
 * Description :
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class PaymentResponse {

    private final String     name;
    private final String     orderNumber;
    private final String     paymentUid;
    private final String     method;
    private final BigDecimal paidAmount;
    private final String     createdDate;

    private PaymentResponse(final Payment payment) {
        this.name = payment.getCustomer().getName();
        this.orderNumber = payment.getOrder().getOrderNumber();
        this.paymentUid = payment.getPaymentUid();
        this.method = payment.getMethod().name();
        this.paidAmount = payment.getPaidAmount();
        this.createdDate = Ut.Str.localDateTimeToString(payment.getCreatedDate());
    }

    public static PaymentResponse of(final Payment payment) {
        return new PaymentResponse(payment);
    }

}
