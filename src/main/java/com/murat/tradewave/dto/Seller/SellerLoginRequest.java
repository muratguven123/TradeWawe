package com.murat.tradewave.dto.Seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerLoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank private String password;
}
