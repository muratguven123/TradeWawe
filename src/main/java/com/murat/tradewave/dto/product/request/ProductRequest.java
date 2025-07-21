package com.murat.tradewave.dto.product.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductRequest {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    private Long categoryId;


}
