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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Payments")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @Column(name = "payment_uid", nullable = false, unique = true)
    private String paymentUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod method;

    @Column(name = "payment_paid_amount", nullable = false)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    public static Payment of(final Order order,
                             final User customer,
                             final String paymentUid,
                             final PaymentMethod method,
                             final BigDecimal paidAmount) {
        Payment payment = Payment.builder()
                                 .order(order)
                                 .paymentUid(paymentUid)
                                 .method(method)
                                 .paidAmount(paidAmount)
                                 .status(PaymentStatus.SUCCESS)
                                 .build();
        payment.customer = customer;
//        customer.getPayments().add(this);   //TODO: 연관관계 설정 확인 필요
        return payment;
    }

    /**
     * 결제 상태 수정
     *
     * @param newPaymentStatus - 새로운 결제 상태
     */
    public Payment updatePaymentStatus(final PaymentStatus newPaymentStatus) {
        if (status != newPaymentStatus)
            status = newPaymentStatus;
        return this;
    }

}
