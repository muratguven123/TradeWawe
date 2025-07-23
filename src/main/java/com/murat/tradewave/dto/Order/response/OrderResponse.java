package com.murat.tradewave.dto.Order.response;

import com.murat.tradewave.dto.OrderItem.Response.OrderItemResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
