package com.murat.tradewave.dto.Cart;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {
    private Long userid;
    private Long productid;
    private int quantity;
}
