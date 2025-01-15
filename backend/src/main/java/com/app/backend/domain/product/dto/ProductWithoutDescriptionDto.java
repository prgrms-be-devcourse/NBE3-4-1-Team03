package com.app.backend.domain.product.dto;

import com.app.backend.domain.product.entity.Product;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithoutDescriptionDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private boolean status;

    public ProductWithoutDescriptionDto(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus();
    }
}
