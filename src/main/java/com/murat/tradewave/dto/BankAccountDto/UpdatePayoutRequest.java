package com.murat.tradewave.dto.BankAccountDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePayoutRequest {
    @NotBlank
    @Size(max = 34)
    public String iban;
    @NotBlank @Size(max = 120)
    public String accountHolderName;
    public String bankName;
    public String currency = "TRY";
}
