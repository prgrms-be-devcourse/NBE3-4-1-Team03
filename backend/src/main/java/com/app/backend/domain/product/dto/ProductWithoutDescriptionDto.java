package com.app.backend.domain.product.dto;

import com.app.backend.domain.product.entity.Product;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductWithoutDescriptionDto {
    private Long product_id;
    private String product_name;
    private BigDecimal product_price;
    private int product_amount;
    private boolean product_status;

    public ProductWithoutDescriptionDto(Product product){
        this.product_id = product.getId();
        this.product_name = product.getName();
        this.product_price = product.getPrice();
        this.product_amount = product.getStock();
        this.product_status = product.getStatus();
    }
}
