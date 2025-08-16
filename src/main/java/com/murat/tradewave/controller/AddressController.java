package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.service.AddresServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddresServiceImpl addresServiceImpl;

    @GetMapping
    public List<Address> findAll() {
        return addresServiceImpl.getAllAdress();
    }
    @GetMapping("/address/{id}")
    public ResponseEntity<Address> findById(@PathVariable Long id) {
        return addresServiceImpl.getAddressByid(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @PostMapping("/save")
    public void save(@RequestBody AddressRequest address) {
        addresServiceImpl.addToAddress(address);
    }
    @PutMapping("/update")
    public void update(@RequestBody AddressRequest address) {
        addresServiceImpl.addToAddress(address);
    }
    @DeleteMapping("/delete")
    public void delete(@RequestBody AddressRequest address) {
        addresServiceImpl.removeFromAddress(address);
    }
}
