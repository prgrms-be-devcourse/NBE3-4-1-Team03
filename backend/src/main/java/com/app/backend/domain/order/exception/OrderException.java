package com.app.backend.domain.order.exception;

import com.app.backend.global.error.exception.DomainException;
import com.app.backend.global.error.exception.ErrorCode;

/**
 * PackageName : com.app.backend.domain.order.exception
 * FileName    : OrderException
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description : 주문 예외
 */
public class OrderException extends DomainException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
