package com.murat.tradewave.helper;

import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.model.Product;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }
}
