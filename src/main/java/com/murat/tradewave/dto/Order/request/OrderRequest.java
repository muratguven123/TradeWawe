package com.murat.tradewave.dto.Order.request;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import com.murat.tradewave.dto.OrderItem.Response.OrderItemResponse;
import com.murat.tradewave.model.OrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    public static OrderRequest OrderRequestBuilder;
    private Long orderId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

