package com.murat.tradewave.dto.Seller;

import com.murat.tradewave.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerRegisterRequest {
    private String sellerName;
    @NotBlank
    private String sellerPassword;
    @NotBlank
    @Email
    private String sellerEmail;

    private Role accountType;
}
