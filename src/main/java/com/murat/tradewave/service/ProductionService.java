package com.murat.tradewave.service;

import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductionService {
    ProductResponse createProduct(ProductRequest productRequest);
    Page<ProductResponse> getAlProducts(int page,int size,String sortBy,String direction);
    ProductResponse getProduct(Long productId);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
}
