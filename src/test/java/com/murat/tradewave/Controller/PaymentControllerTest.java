package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.PaymentController;
import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.exception.OrderAlreadyPaidException;
import com.murat.tradewave.exception.OrderNotFoundException;
import com.murat.tradewave.security.JwtAuthenticationFilter;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PaymentController Tests - Edge Cases & Error Scenarios")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        // Updated PaymentRequest - now only contains orderId
        paymentRequest = PaymentRequest.builder()
                .orderId(1L)
                .build();

        paymentResponse = PaymentResponse.builder()
                .paymentId(100L)
                .orderId(1L)
                .amount(new BigDecimal("150.00"))
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /payment/send - Should return 401 when not authenticated")
    void initialPayment_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(paymentService);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should return 400 when orderId is null")
    void initialPayment_ShouldReturn400_WhenOrderIdIsNull() throws Exception {
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(null)
                .build();

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should return 404 when order not found")
    void initialPayment_ShouldReturn404_WhenOrderNotFound() throws Exception {
        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found"));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should return 400 when order already paid")
    void initialPayment_ShouldReturn400_WhenOrderAlreadyPaid() throws Exception {
        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenThrow(new OrderAlreadyPaidException("Order already exists"));

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Order Already Paid"))
                .andExpect(jsonPath("$.message").value("Order already exists"));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should process payment successfully")
    void initialPayment_ShouldProcessPayment_WhenRequestIsValid() throws Exception {
        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(100))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.status").value("Success"));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should handle failed payment correctly")
    void initialPayment_ShouldHandleFailedPayment() throws Exception {
        PaymentResponse failedResponse = PaymentResponse.builder()
                .paymentId(101L)
                .orderId(1L)
                .amount(new BigDecimal("150.00"))
                .status("Failed")
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenReturn(failedResponse);

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(101))
                .andExpect(jsonPath("$.status").value("Failed"));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should return 500 when unexpected error occurs")
    void initialPayment_ShouldReturn500_WhenUnexpectedErrorOccurs() throws Exception {
        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should handle malformed JSON")
    void initialPayment_ShouldReturn400_WhenJsonIsMalformed() throws Exception {
        String malformedJson = "{\"orderId\": \"invalid\"}";

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paymentService);
    }

    @Test
    @DisplayName("GET /payment/history - Should return 401 when not authenticated")
    void getPaymentHistory_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/payment/history"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(paymentService);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("GET /payment/history - Should return empty list when no payment history")
    void getPaymentHistory_ShouldReturnEmptyList_WhenNoHistory() throws Exception {
        when(paymentService.paymentGetHistory("user@example.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/payment/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(paymentService, times(1)).paymentGetHistory("user@example.com");
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("GET /payment/history - Should return payment history list")
    void getPaymentHistory_ShouldReturnList_WhenHistoryExists() throws Exception {
        PaymentResponse payment1 = PaymentResponse.builder()
                .paymentId(1L)
                .orderId(10L)
                .amount(new BigDecimal("50.00"))
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();

        PaymentResponse payment2 = PaymentResponse.builder()
                .paymentId(2L)
                .orderId(11L)
                .amount(new BigDecimal("75.00"))
                .status("Failed")
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentService.paymentGetHistory("user@example.com"))
                .thenReturn(List.of(payment1, payment2));

        mockMvc.perform(get("/payment/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].paymentId").value(1))
                .andExpect(jsonPath("$[0].orderId").value(10))
                .andExpect(jsonPath("$[0].amount").value(50.00))
                .andExpect(jsonPath("$[0].status").value("Success"))
                .andExpect(jsonPath("$[1].paymentId").value(2))
                .andExpect(jsonPath("$[1].status").value("Failed"));

        verify(paymentService, times(1)).paymentGetHistory("user@example.com");
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("GET /payment/history - Should return 500 when service throws exception")
    void getPaymentHistory_ShouldReturn500_WhenServiceThrowsException() throws Exception {
        when(paymentService.paymentGetHistory(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/payment/history"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));

        verify(paymentService, times(1)).paymentGetHistory("user@example.com");
    }

    @Test
    @WithMockUser(username = "different@user.com")
    @DisplayName("GET /payment/history - Should use correct username from authentication")
    void getPaymentHistory_ShouldUseCorrectUsername() throws Exception {
        when(paymentService.paymentGetHistory("different@user.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/payment/history"))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).paymentGetHistory("different@user.com");
        verify(paymentService, never()).paymentGetHistory("user@example.com");
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should handle large order amounts")
    void initialPayment_ShouldHandleLargeAmounts() throws Exception {
        PaymentResponse largeAmountResponse = PaymentResponse.builder()
                .paymentId(200L)
                .orderId(1L)
                .amount(new BigDecimal("999999.99"))
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentService.initailPayment(any(PaymentRequest.class)))
                .thenReturn(largeAmountResponse);

        mockMvc.perform(post("/payment/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(999999.99));

        verify(paymentService, times(1)).initailPayment(any(PaymentRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("POST /payment/send - Should handle missing Content-Type header")
    void initialPayment_ShouldReturn415_WhenContentTypeIsMissing() throws Exception {
        mockMvc.perform(post("/payment/send")
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(paymentService);
    }
}
