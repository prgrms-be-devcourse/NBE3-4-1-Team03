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
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Orders")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @Column(unique = true, nullable = false, updatable = false)
    private String orderNumber;

    @Column(name = "order_total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "order_total_price", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.00")
    private BigDecimal totalPrice;

    @Column(name = "order_address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    public static Order of(final User customer,
                           final String orderNumber,
                           final int totalAmount,
                           final BigDecimal totalPrice,
                           final String address) {
        Order order = Order.builder()
                           .orderNumber(orderNumber)
                           .totalAmount(totalAmount)
                           .totalPrice(totalPrice)
                           .address(address)
                           .status(OrderStatus.ORDERED)
                           .build();
        order.customer = customer;
//        customer.getOrders().add(order);    //TODO: 연관관계 설정 확인 필요
        return order;
    }

    /**
     * 주문 상태 수정
     *
     * @param newOrderStatus - 새로운 주문 상태
     * @return 주문 정보 엔티티
     */
    public Order updateOrderStatus(final OrderStatus newOrderStatus) {
        if (status != newOrderStatus)
            status = newOrderStatus;
        return this;
    }

    public void updateTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
