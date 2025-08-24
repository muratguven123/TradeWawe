package com.murat.tradewave.dto.Cart;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Setter@Getter
public class RemoveCartRequest {
    private Long userid;
    private Long productid;
}
