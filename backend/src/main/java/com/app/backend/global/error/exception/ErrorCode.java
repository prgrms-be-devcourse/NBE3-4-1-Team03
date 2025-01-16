package com.app.backend.global.error.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    //Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, false, "C001", "올바르지 않은 입력값"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, false, "C002", "올바르지 않은 HTTP 메서드"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, false, "C003", "값을 찾지 못함"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, false, "C004", "요청이 거부됨"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, "C005", "서버 내부 오류 발생"),

    //User
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, false, "U001", "이미 사용중인 이메일"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "U002", "회원 정보가 존재하지 않음"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, false, "U003", "잘못된 자격 증명"),
    PASSWORD_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST, false, "U004", "새 비밀번호가 현재 비밀번호와 동일"),
    USER_DELETED(HttpStatus.BAD_REQUEST, false, "U005", "탈퇴한 회원"),

    //Product
    PRODUCT_DUPLICATION(HttpStatus.BAD_REQUEST, false, "P001", "이미 존재하는 제품"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "P002", "제품 정보가 존재하지 않음"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, false, "P003", "제품 재고 부족"),
    PRODUCT_SORT_NOT_EXISTS(HttpStatus.NOT_FOUND, false, "P004", "요청한 정렬 조건이 존재하지 않음"),
    PRODUCT_DIRECTION_NOT_EXISTS(HttpStatus.NOT_FOUND, false, "P005", "요청한 정렬 방향이 존재하지 않음"),

    //Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, false, "O001", "주문 정보가 존재하지 않음"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, false, "O002", "잘못된 주문 상태"),

    //Payment
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, false, "E001", "결제 실패"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, "E002", "결제 정보가 존재하지 않음"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, false, "E003", "잔액이 부족함");

    private final HttpStatus status;
    private final boolean    isSuccess;
    private final String     code;
    private final String     message;

}
