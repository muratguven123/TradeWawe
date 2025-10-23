package com.murat.tradewave.dto.OrderItem.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
private Long productId;
private String name;
private Integer quantity;

}
