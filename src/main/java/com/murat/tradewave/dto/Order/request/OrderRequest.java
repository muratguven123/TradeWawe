package com.murat.tradewave.dto.Order.request;

import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private List<OrderItemRequest> items;

}
