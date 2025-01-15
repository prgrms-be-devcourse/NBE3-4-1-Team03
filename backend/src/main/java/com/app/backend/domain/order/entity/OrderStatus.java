package com.app.backend.domain.order.entity;

/**
 * PackageName : com.app.backend.domain.order.entity
 * FileName    : OrderStatus
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description : 주문 상태
 */
public enum OrderStatus {
    ORDERED,    //주문 완료
    SHIPPED,    //배송 중
    DELIVERED,  //배송 완료
    CANCELLED   //주문 취소
}
