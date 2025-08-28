package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.OrderController;
import com.murat.tradewave.dto.Order.request.OrderRequest;
import com.murat.tradewave.dto.Order.response.OrderResponse;
import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;

    @Test
    void getOrderDetail_shouldReturnOk() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .status("Created")
                .totalAmount(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();
        when(orderService.getOrderDetail(1L, "user@example.com")).thenReturn(response);

        mockMvc.perform(get("/orders/1").with(user("user@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1));

        verify(orderService).getOrderDetail(1L, "user@example.com");
    }

    @Test
    void createOrder_shouldReturnOk() throws Exception {
        OrderItemRequest request = OrderItemRequest.builder()
                .productıd(1L)
                .name("Test Product")
                .quantity(2)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .orderId(1L)
                .status(OrderStatus.Created)
                .totalAmount(BigDecimal.TEN)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.createOrder(any(OrderItemRequest.class), eq("user@example.com"))).thenReturn(orderRequest);

        mockMvc.perform(post("/orders/create")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1));

        verify(orderService).createOrder(any(OrderItemRequest.class), eq("user@example.com"));
    }
}