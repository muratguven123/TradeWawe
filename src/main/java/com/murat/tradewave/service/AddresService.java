package com.murat.tradewave.service;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.dto.Address.response.AdressResponse;

import java.util.List;

public interface AddresService {
    void addToAddress(AddressRequest addressRequest);
    void removeFromAddress(AddressRequest addressRequest);
    List<AdressResponse> getAdress(AdressResponse addressRequest);
    AdressResponse updateAddress(AdressResponse addresponse);
}
