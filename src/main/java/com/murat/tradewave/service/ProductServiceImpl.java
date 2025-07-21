package com.murat.tradewave.service;

import com.murat.tradewave.dto.category.request.CategoryRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.repository.CategoryRepository;
import com.murat.tradewave.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.murat.tradewave.helper.mapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductionService{
    private final ProductionRepository productionRepository;
    private final mapper mapper;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId()).get();
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .createdAt(LocalDateTime.now())
                .stock(productRequest.getStock())
                .category(category)
                .build();
        Product savedProduct = productionRepository.save(product);
        return mapper.mapToResponse(savedProduct);
    }
    @Override
    public Page<ProductResponse> getAlProducts(int page, int size, String sortBy, String direction) {
        Sort sort= direction.equals("desc") ?
                Sort.by(sortBy).descending():Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return productionRepository.findAll(pageable).map(mapper::mapToResponse);
    }

    @Override
    public ProductResponse getProduct(Long productId) {
        Product product = productionRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not found"));
        return mapper.mapToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest){
        Product product=productionRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found, Please be sure search true product in our system"));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        productionRepository.save(product);
        return mapper.mapToResponse(product);
    }
    public void deleteProduct(Long id){
        Product deletedProduct=productionRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found,Please be sure search true product in our system"));
        productionRepository.delete(deletedProduct);
    }
}
