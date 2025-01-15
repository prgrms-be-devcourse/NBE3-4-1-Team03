package com.app.backend.domain.order.entity;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.app.backend.domain.order.entity
 * FileName    : Payment
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description : 결제 정보 엔티티
 */
@Entity
@Table(name = "Payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private Long id;    //결제 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    //주문 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;  //주문 회원

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod method;   //결제 수단

    @Column(name = "payment_paid_amount", nullable = false)
    private BigDecimal paidAmount;  //결제 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;   //결제 상태: [SUCCESS, FAILED]

    private Payment(final Order order,
                    final User customer,
                    final PaymentMethod method,
                    final BigDecimal paidAmount,
                    final PaymentStatus status) {
        setRelationshipWithOrder(order);
        setRelationshipWithUser(customer);
        this.customer = customer;
        this.method = method;
        this.paidAmount = paidAmount;
        this.status = status;
    }

    public static Payment of(final Order order,
                             final User customer,
                             final PaymentMethod method,
                             final BigDecimal paidAmount,
                             final PaymentStatus status) {
        return new Payment(order, customer, method, paidAmount, status);
    }

    //==================== 연관관계 메서드 ====================//

    private void setRelationshipWithOrder(final Order order) {
        this.order = order;
//        order.getPayments().add(this);
    }

    private void setRelationshipWithUser(final User user) {
        this.customer = user;
//        user.getPayments().add(this);
    }

}
