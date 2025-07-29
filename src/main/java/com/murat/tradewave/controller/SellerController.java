package com.murat.tradewave.controller;

import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.service.ProductServiceImpl;
import com.murat.tradewave.service.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final ProductServiceImpl productService;
    private final ProductionService productionService;

    @GetMapping("/id")
    @PreAuthorize("hasRole('Seller')")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productionService.getProduct(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('Seller')")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('Seller')")
    public void deleteProduct(@PathVariable @Valid Long id) {
        productService.deleteProduct(id);
    }
}
