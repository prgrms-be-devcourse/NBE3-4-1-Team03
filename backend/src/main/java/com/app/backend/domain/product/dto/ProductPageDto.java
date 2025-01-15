package com.app.backend.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ProductPageDto<T> extends PageDto<T>{
    @JsonProperty("product_info")
    private final List<T> items;

    private final Boolean hasNext;
    private final Boolean hasPrevious;
    private final Boolean isLast;

    public ProductPageDto(Page<T> postPage) {
        super(postPage);
        this.items = super.getItems();
        this.hasNext=postPage.hasNext();
        this.hasPrevious=postPage.hasPrevious();
        this.isLast=postPage.isLast();
    }

    @Override
    public List<T> getItems() {
        return items;
    }
}
