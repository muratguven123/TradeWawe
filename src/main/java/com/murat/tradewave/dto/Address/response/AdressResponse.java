package com.murat.tradewave.dto.Address.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdressResponse {
    private String addresName;
    private Long id;
    private String title;
    private String street;
    private String city;
    private String discrit;
    private String postalCode;
    private String country;
    private boolean isDefault;




}
