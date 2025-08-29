package com.murat.tradewave.Service;

import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.repository.CategoryRepository;
import com.murat.tradewave.repository.ProductionRepository;
import com.murat.tradewave.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductionRepository productionRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private Category category;
    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("category").build();
    }
    @Test
    void createProduct_shouldSaveAndReturnResponse() {
        ProductRequest request = ProductRequest.builder()
                .name("product")
                .description("desc")
                .price(BigDecimal.TEN)
                .stock(5)
                .categoryId(category.getId())
                .build();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        Product saved = Product.builder()
                .id(2L)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .stock(request.getStock())
                .category(category)
                .build();
        when(productionRepository.save(any(Product.class))).thenReturn(saved);
        ProductResponse expected = ProductResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .stock(saved.getStock())
                .build();
        when(mapper.mapToResponse(saved)).thenReturn(expected);
        ProductResponse result = productService.createProduct(request);
        assertThat(result).isEqualTo(expected);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productionRepository).save(captor.capture());
        Product created = captor.getValue();
        assertThat(created.getName()).isEqualTo(request.getName());
        assertThat(created.getCategory()).isEqualTo(category);
    }
    @Test
    void getAllProducts_shouldReturnMappedPage() {
        Product p1 = Product.builder().id(1L).name("A").description("a").price(BigDecimal.ONE).stock(1).build();
        Product p2 = Product.builder().id(2L).name("B").description("b").price(BigDecimal.TWO).stock(2).build();
        List<Product> products = Arrays.asList(p1, p2);
        Page<Product> page = new PageImpl<>(products);

        PageRequest pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
        when(productionRepository.findAll(pageable)).thenReturn(page);
        when(mapper.mapToResponse(p1)).thenReturn(ProductResponse.builder().id(1L).name("A").description("a").price(BigDecimal.ONE).stock(1).build());
        when(mapper.mapToResponse(p2)).thenReturn(ProductResponse.builder().id(2L).name("B").description("b").price(BigDecimal.TWO).stock(2).build());

        Page<ProductResponse> result = productService.getAlProducts(0, 2, "name", "asc");

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("A");
        verify(productionRepository).findAll(pageable);
    }
    @Test
    void getProduct_shouldReturnResponse() {
        Product product = Product.builder().id(5L).name("P").description("d").price(BigDecimal.ONE).stock(3).build();
        ProductResponse response = ProductResponse.builder().id(5L).name("P").description("d").price(BigDecimal.ONE).stock(3).build();

        when(productionRepository.findById(5L)).thenReturn(Optional.of(product));
        when(mapper.mapToResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProduct(5L);

        assertThat(result).isEqualTo(response);
    }
    @Test
    void getProduct_shouldThrowWhenNotFound() {
        when(productionRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");
    }
    @Test
    void updateProduct_shouldUpdateAndReturnResponse() {
        Product existing = Product.builder().id(3L).name("Old").description("Old").price(BigDecimal.ONE).stock(1).build();
        ProductRequest request = ProductRequest.builder()
                .name("New")
                .description("New")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();
        ProductResponse response = ProductResponse.builder().id(3L).name("New").description("New").price(BigDecimal.TEN).stock(5).build();
        when(productionRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(mapper.mapToResponse(existing)).thenReturn(response);
        ProductResponse result = productService.updateProduct(3L, request);
        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getPrice()).isEqualTo(BigDecimal.TEN);
        verify(productionRepository).save(existing);
        assertThat(result).isEqualTo(response);
    }
    @Test
    void deleteProduct_shouldDeleteExistingProduct() {
        Product existing = Product.builder().id(4L).build();
        when(productionRepository.findById(4L)).thenReturn(Optional.of(existing));

        productService.deleteProduct(4L);

        verify(productionRepository).delete(existing);
    }
}
