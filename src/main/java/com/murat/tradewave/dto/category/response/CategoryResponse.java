package com.murat.tradewave.dto.category.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private String categoryName;
    private Long categoryId;
}
