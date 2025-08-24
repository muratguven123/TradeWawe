package com.murat.tradewave.dto.BankAccountDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdatePayoutResponse {
    @NotBlank
    @Size(max = 34)
    public String iban;
    @NotBlank @Size(max = 120)
    public String accountHolderName;
    public String bankName;
    public String currency = "TRY";


}
