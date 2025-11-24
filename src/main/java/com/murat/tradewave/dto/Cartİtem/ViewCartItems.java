package com.murat.tradewave.dto.Cartİtem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewCartItems {
    private Long cartId;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
}

