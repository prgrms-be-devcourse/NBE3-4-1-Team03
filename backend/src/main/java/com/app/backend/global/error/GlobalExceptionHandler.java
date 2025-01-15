package com.app.backend.global.error;

import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.global.error.exception.DomainException;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rs.RsData;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 지원하지 않는 HTTP Method 호출 시(자바 서블릿)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<RsData<Void>> handleMethodNotAllowedException(MethodNotAllowedException e) {
        log.error("handleMethodNotAllowedException", e);
        final ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus())
                             .body(new RsData<>(false,
                                                errorCode.getMessage(),
                                                errorCode.getCode()));
    }

    /**
     * 지원하지 않는 HTTP Method 호출 시(스프링)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RsData<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus())
                             .body(new RsData<>(false,
                                                errorCode.getMessage(),
                                                errorCode.getCode()));
    }

    /**
     * BindException 발생 시(@Valid 또는 @Validated 에서 바인딩 에러)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<RsData<Void>> handleBindException(BindException e) {
        log.error("handleBindException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(errorCode.getStatus())
                             .body(new RsData<>(false,
                                                errorCode.getMessage(),
                                                errorCode.getCode()));
    }

    /**
     * 주문(Order) 관련 예외 발생 시
     *
     * @param e
     * @return
     */
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<RsData<Void>> handleOrderException(OrderException e) {
        log.error("handleOrderException", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                             .body(new RsData<>(false,
                                                e.getErrorCode().getMessage(),
                                                e.getErrorCode().getCode()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<RsData<Void>> handleDomainException(DomainException e) {
        log.error("handleDomainException", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                             .body(new RsData<>(false,
                                                e.getErrorCode().getMessage(),
                                                e.getErrorCode().getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        log.error("handleException", e);
        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.internalServerError()
                             .body(new RsData<>(false,
                                                errorCode.getMessage(),
                                                errorCode.getCode()));
    }


    /**
     * 유저(User) 관련 예외 발생 시
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RsData<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("handleConstraintViolationException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(errorCode.getStatus())
                .body(new RsData<>(false,
                        errorCode.getMessage(),
                        errorCode.getCode()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RsData<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException", e);
        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(errorCode.getStatus())
                .body(new RsData<>(false,
                        errorCode.getMessage(),
                        errorCode.getCode()));
    }

}
