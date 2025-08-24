package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.repository.SellerRepository;
import com.murat.tradewave.service.SellerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerServiceImpl sellerService;
    private final SellerRepository sellerRepository;

    @GetMapping("{id}")
    public Seller getSellerByid(@PathVariable Long id) {
        return sellerService.get(id);
    }
    @PostMapping
    public SellerRequest createSeller(@RequestBody SellerRequest seller) {
        return sellerService.register(seller);
    }
    @PostMapping
    public User loginSeller(@RequestBody SellerRequest seller) {
        return sellerService.login(seller);
    }

}
