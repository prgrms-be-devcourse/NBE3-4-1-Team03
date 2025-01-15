package com.app.backend.domain.product.exception;

import com.app.backend.global.error.exception.DomainException;
import com.app.backend.global.error.exception.ErrorCode;

public class ProductException extends DomainException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
