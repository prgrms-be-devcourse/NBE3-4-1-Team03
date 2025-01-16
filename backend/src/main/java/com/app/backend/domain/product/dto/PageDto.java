package com.app.backend.domain.product.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageDto<T>{
    private final List<T> items;
    private final long totalItems;
    private final long totalPages;
    private final long currentPage;
    private final long pageSize;
    public PageDto(Page<T> postPage) {
        this.items = postPage.getContent().stream().toList();
        this.totalItems= postPage.getTotalElements();
        this.totalPages= postPage.getTotalPages();
        this.currentPage=postPage.getNumber()+1;
        this.pageSize= postPage.getSize();
    }
}
