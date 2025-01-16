package com.app.backend.domain.order.entity;

import com.app.backend.domain.product.entity.Product;
import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Order_Products")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "order_product_amount", nullable = false)
    private int productAmount;

    @Column(name = "order_product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "order_product_total_price", nullable = false)
    private BigDecimal totalProductPrice;

    public static OrderProduct of(final Order order,
                                  final Product product,
                                  final int productAmount,
                                  final BigDecimal productPrice) {
        OrderProduct orderProduct = OrderProduct.builder()
                                                .productAmount(productAmount)
                                                .productPrice(productPrice)
                                                .build();
        orderProduct.order = order;
        order.getOrderProducts().add(orderProduct);
        orderProduct.product = product;
//        product.getOrderProducts().add(orderProduct);   //TODO: 연관관계 설정 확인 필요
        orderProduct.totalProductPrice = productPrice.multiply(BigDecimal.valueOf(productAmount));
        return orderProduct;
    }
    //TODO: 제품(Product) 주문 발생 시(= OrderProduct 엔티티 생성 시) 재고 수량 감소 병행 필요, 재고 수량이 주문 수량보다 작다면?

}
