package com.murat.tradewave.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StoreResponse {
    @NotNull
    private Long storeid;
    @NotBlank
    private String storeName;
    @NotBlank
    private String storeAddress;
    @NotBlank
    private String description;
    @NotBlank
    private String email;
    @NotBlank
    private String storePhone;
    @NotBlank
    private String location;
    @NotNull
    private Long averageResponseTime;
    @NotNull
    private Long averageDeliveryTime;
}
