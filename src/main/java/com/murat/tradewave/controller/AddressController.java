package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.service.AddresServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddresServiceImpl addresServiceImpl;


    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<AdressResponse>> findAll() {
        List<Address> addresses = addresServiceImpl.getAllAdress();
        List<AdressResponse> response = addresses.stream()
                .map(address -> AdressResponse.builder()
                        .id(address.getId())
                        .addresName(address.getName())
                        .title(address.getTitle())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .discrit(address.getDiscrict())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry())
                        .isDefault(address.isDefault())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/address/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AdressResponse> findById(@PathVariable Long id) {
        return addresServiceImpl.getAddressByid(id)
                .map(address -> AdressResponse.builder()
                        .id(address.getId())
                        .addresName(address.getName())
                        .title(address.getTitle())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .discrit(address.getDiscrict())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry())
                        .isDefault(address.isDefault())
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody AddressRequest address) {
        addresServiceImpl.addToAddress(address);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<AdressResponse> update(@RequestBody AdressResponse address) {
        AdressResponse updated = addresServiceImpl.updateAddress(address);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody AddressRequest address) {
        addresServiceImpl.removeFromAddress(address);
        return ResponseEntity.ok().build();
    }
}
