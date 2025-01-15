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
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.app.backend.domain.order.entity
 * FileName    : OrderProduct
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description : 주문 제품 엔티티
 */
@Entity
@Table(name = "Order_Products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id", nullable = false, updatable = false)
    private Long id;    //주문 제품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    //주문 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;    //제품 정보

    @Column(name = "order_product_amount", nullable = false)
    private int productAmount;  //제품 수량

    @Column(name = "order_product_price", nullable = false)
    private BigDecimal productPrice;    //제품 가격

    @Column(name = "order_product_total_price", nullable = false)
    private BigDecimal totalProductPrice;   //제품 가격 총합

    private OrderProduct(final Order order,
                         final Product product,
                         final int productAmount,
                         final BigDecimal productPrice,
                         final BigDecimal totalProductPrice) {
        setRelationshipWithOrder(order);
        setRelationshipWithProduct(product);
        this.productAmount = productAmount;
        this.productPrice = productPrice;
        this.totalProductPrice = totalProductPrice;
    }

    public static OrderProduct of(final Order order,
                                  final Product product,
                                  final int productAmount,
                                  final BigDecimal productPrice,
                                  final BigDecimal totalProductPrice) {
        return new OrderProduct(order, product, productAmount, productPrice, totalProductPrice);
    }

    //==================== 연관관계 메서드 ====================//

    private void setRelationshipWithOrder(final Order order) {
        this.order = order;
        order.getOrderProducts().add(this);
    }

    private void setRelationshipWithProduct(final Product product) {
        this.product = product;
//        product.getOrderProduct().add(this);
    }

}
