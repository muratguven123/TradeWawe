package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.service.AddresServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddresServiceImpl addresServiceImpl;

    @GetMapping("/{id}")
    public Optional<Address> findById(@PathVariable Long id) {
        return addresServiceImpl.getAddressByid(id);
    }
    @GetMapping
    public List<Address> findAll() {
        return addresServiceImpl.getAllAdress();
    }
    @PostMapping
    public void save(@RequestBody AddressRequest address) {
        addresServiceImpl.addToAddress(address);
    }
    @PutMapping
    public void update(@RequestBody AddressRequest address) {
        addresServiceImpl.addToAddress(address);
    }
    @DeleteMapping
    public void delete(@RequestBody AddressRequest address) {
        addresServiceImpl.removeFromAddress(address);
    }
}
