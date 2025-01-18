package com.app.backend.domain.order.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.app.backend.domain.order.dto.request
 * FileName    : PaymentRequest
 * Author      : 강찬우
 * Date        : 25. 1. 18.
 * Description :
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String     paymentUid;
    private String     method;
    private BigDecimal paidAmount;
}
