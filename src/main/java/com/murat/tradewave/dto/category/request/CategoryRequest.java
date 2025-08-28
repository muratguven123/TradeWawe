package com.murat.tradewave.dto.category.request;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CategoryRequest {
    private String name;
    private Long id;
}
