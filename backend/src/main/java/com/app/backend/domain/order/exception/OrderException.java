package com.app.backend.domain.order.exception;

import com.app.backend.global.error.exception.DomainException;
import com.app.backend.global.error.exception.ErrorCode;
import lombok.Getter;

@Getter
public class OrderException extends DomainException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
