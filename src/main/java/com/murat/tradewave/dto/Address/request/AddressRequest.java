package com.murat.tradewave.dto.Address.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressRequest {
    @NotBlank private String name;
    @NotBlank private String title;
    @NotBlank private String street;
    @NotBlank private String city;
    private String district;
    private String postalCode;
    private String country;
    private boolean isDefault;

}
