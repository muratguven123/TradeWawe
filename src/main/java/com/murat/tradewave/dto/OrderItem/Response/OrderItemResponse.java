package com.murat.tradewave.dto.OrderItem.Response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long productİd;

    private String productName;

    private Integer quantity;

    private BigDecimal price;



}
