package com.murat.tradewave.dto.Address.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressRequest {
    @NotBlank
    private Long id;
    @NotBlank
    private String addresName;
    @NotBlank
    private String tittle;
    @NotBlank
    private String street;
    private String city;
    private String state;
    private String discrict;
    private String postalCode;
    private String country;
    private boolean isDefault;

    private LocalDateTime createdAt;
}
