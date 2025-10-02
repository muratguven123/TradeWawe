package com.murat.tradewave.controller;

import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.service.ProductServiceImpl;
import com.murat.tradewave.service.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductionService productionService;
    private final ProductServiceImpl productServiceimpl;

    @PostMapping("/create")
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productServiceimpl.createProduct(productRequest);
    }
    @GetMapping("/all")
    public Page<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction){
        return productServiceimpl.getAlProducts(page, size, sort, direction);
    }
        @GetMapping("/{id}")
        public ProductResponse getProduct(@PathVariable Long id) {
        return productionService.getProduct(id);
        }
        @PutMapping("/update")
        public ProductResponse updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        return productServiceimpl.updateProduct(id, productRequest);
        }
        @DeleteMapping("/delete")
        public void deleteProduct(@PathVariable @Valid Long id) {
            productServiceimpl.deleteProduct(id);
        }

}
