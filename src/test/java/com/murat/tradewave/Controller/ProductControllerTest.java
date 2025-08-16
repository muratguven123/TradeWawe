package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.ProductController;
import com.murat.tradewave.dto.product.request.ProductRequest;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.service.ProductServiceImpl;
import com.murat.tradewave.service.ProductionService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Nested
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductionService productionService;

    @MockBean
    private ProductServiceImpl productService;

    @Test
    void createProduct_shouldReturnOk() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.TEN)
                .stock(5)
                .categoryId(1L)
                .build();
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();
        when(productionService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productionService).createProduct(any(ProductRequest.class));
    }

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();
        Page<ProductResponse> page = new PageImpl<>(List.of(response));
        when(productionService.getAlProducts(0,6,"createdAt","desc")).thenReturn(page);

        mockMvc.perform(get("/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

        verify(productionService).getAlProducts(0,6,"createdAt","desc");
    }

    @Test
    void getProduct_shouldReturnOk() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming laptop")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();
        when(productionService.getProduct(1L)).thenReturn(response);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productionService).getProduct(1L);
    }
}
