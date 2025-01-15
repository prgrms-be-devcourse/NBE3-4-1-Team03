package com.app.backend.domain.order.entity;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.app.backend.domain.order.entity
 * FileName    : Order
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description : 주문 정보 엔티티
 */
@Entity
@Table(name = "Orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, updatable = false)
    private Long id;    //주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;  //주문 회원

    @Column(nullable = false)
    private String orderNumber; //주문 번호

    @Column(name = "order_total_amount", nullable = false)
    private int totalAmount;    //전체 주문 수량

    @Column(name = "order_total_price", nullable = false)
    private BigDecimal totalPrice;  //전체 주문 금액

    @Column(name = "order_address", nullable = false)
    private String address; //배송 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status; //주문 상태: [Ordered, Shipped, Delivered, Cancelled]

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();   //주문 제품 목록

    private Order(final User customer,
                  final String orderNumber,
                  final int totalAmount,
                  final BigDecimal totalPrice,
                  final String address,
                  final OrderStatus status) {
        setRelationshipWithUser(customer);
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
        this.address = address;
        this.status = status;
    }

    public static Order of(final User customer,
                           final String orderNumber,
                           final int totalAmount,
                           final BigDecimal totalPrice,
                           final String address,
                           final OrderStatus status) {
        return new Order(customer,
                         orderNumber,
                         totalAmount,
                         totalPrice,
                         address,
                         status);
    }

    //==================== 연관관계 메서드 ====================//

    private void setRelationshipWithUser(final User user) {
        this.customer = user;
//        user.getOrders().add(this);
    }

    //==================== 수정 메서드 ====================//

    /**
     * 주문 상태 변경
     *
     * @param newStatus - 새로운 주문 상태
     * @return 주문 정보
     */
    public Order updateStatus(final OrderStatus newStatus) {
        if (this.status != newStatus) this.status = newStatus;
        return this;
    }

}
