package com.app.backend.domain.product.dto;

import com.app.backend.domain.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailDto {
    private String name;
    private String description;
    private BigDecimal price;
    @JsonProperty("amount")
    private Integer stock;
    private Boolean status;
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
    @JsonProperty("modified_date")
    private LocalDateTime modifiedDate;

    public ProductDetailDto(Product product){
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus();
        this.createdDate = product.getCreatedDate();
        this.modifiedDate = product.getModifiedDate();
    }
}
