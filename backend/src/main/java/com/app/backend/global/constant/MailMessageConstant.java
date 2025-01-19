package com.app.backend.global.constant;

/**
 * PackageName : com.app.backend.domain.order.constant
 * FileName    : MailMessageConstant
 * Author      : loadingKKamo21
 * Date        : 25. 1. 18.
 * Description :
 */
public abstract class MailMessageConstant {
    public static final String MAIL_SUBJECT_ORDER_SUCCESS = "주문 완료";
    public static final String MAIL_SUBJECT_ORDER_UPDATE  = "주문 정보 업데이트";
    public static final String MAIL_SUBJECT_ORDER_CANCEL  = "주문 취소";

    public static final String MAIL_MESSAGE_ORDER_DELIVERY_PENDING = "당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.";
    public static final String MAIL_MESSAGE_ORDER_SHIPPED          = "배송 시작";
}
