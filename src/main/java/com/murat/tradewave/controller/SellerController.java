package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Seller.SellerLoginRequest;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.repository.SellerRepository;
import com.murat.tradewave.service.SellerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerServiceImpl sellerService;

    @GetMapping("/{id}")
    public Seller getSellerById(@PathVariable Long id) {
        return sellerService.get(id);
    }

    @PostMapping("/register")
    public ResponseEntity<SellerResponse> register(@Valid @RequestBody SellerRegisterRequest request) {
        SellerResponse response = sellerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginSeller(@Valid @RequestBody SellerLoginRequest req) {
        String token = String.valueOf(sellerService.login(req));
        return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
    }

}
