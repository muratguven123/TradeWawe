package com.murat.tradewave.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.SellerController;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.service.SellerServiceImpl;
import com.murat.tradewave.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SellerServiceImpl sellerService;

    @MockBean
    private JwtService jwtService;

    @Test
    void registerSeller_shouldReturnCreated() throws Exception {
        SellerRegisterRequest request = new SellerRegisterRequest();
        request.setSellerName("John");
        request.setSellerEmail("john@example.com");
        request.setSellerPassword("pass");
        request.setAccountType(Role.SELLER);

        SellerResponse response = SellerResponse.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        when(sellerService.register(any(SellerRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/sellers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getSellerById_shouldReturnOk() throws Exception {
        Seller seller = Seller.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("pass")
                .accountType(Role.SELLER)
                .build();

        when(sellerService.get(1L)).thenReturn(seller);

        mockMvc.perform(get("/sellers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
