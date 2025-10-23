package com.murat.tradewave.dto.OrderItem.Response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal price;
}
