package com.murat.tradewave.dto.Seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerResponse {
    @NotNull
    private Long Sellerid;
    @NotBlank
    private String sellerName;
    @NotBlank
    private String sellerPassword;
    private String token;
}
