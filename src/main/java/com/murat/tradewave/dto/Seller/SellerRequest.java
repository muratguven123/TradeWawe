package com.murat.tradewave.dto.Seller;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.BankAccountDto.BankAccountRequest;
import com.murat.tradewave.dto.store.StoreRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SellerRequest {
    @NotNull
    private Long Sellerid;
    @NotBlank
    private String sellerName;
    @NotBlank
    private String sellerPassword;
    private String token;
    @NotBlank
    private String sellerEmail;
}


