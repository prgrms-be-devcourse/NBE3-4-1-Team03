package com.app.backend.domain.user.exception;

import com.app.backend.global.error.exception.DomainException;
import com.app.backend.global.error.exception.ErrorCode;

public class UserException extends DomainException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
