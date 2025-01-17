package com.app.backend.domain.product.entity;

import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

import lombok.*;

@Entity
@Table(name = "Products")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "product_name", unique = true, nullable = false)
    private String name;

    @Column(name = "product_description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "product_image")
    private String image;

    @Column(name = "product_price", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "100.00", message = "")
    private BigDecimal price;

    @Column(name = "product_stock", nullable = false)
    @Min(value = 0, message = "")
    private Integer stock;

    @Column(name = "product_status", nullable = false)
    private Boolean status;

}