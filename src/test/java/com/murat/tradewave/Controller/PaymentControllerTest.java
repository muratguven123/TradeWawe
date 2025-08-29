package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.PaymentController;
import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.security.JwtAuthenticationFilter;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PaymentService paymentService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JwtService jwtService;

    @WithMockUser(username = "user@example.com")
    @Test
    void getPaymentHistory_shouldReturnOk() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .paymentId(1L)
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();
        when(paymentService.paymentGetHistory("user@example.com")).thenReturn(List.of(response));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        mockMvc.perform(get("/payment/history").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentId").value(1));

        verify(paymentService).paymentGetHistory("user@example.com");
    }

    @WithMockUser(username = "user@example.com")
    @Test
    void initialPayment_shouldReturnOk() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .paymentId(1L)
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Success")
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentService.initailPayment(any(PaymentRequest.class))).thenReturn(response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        mockMvc.perform(post("/payment/send")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1));

        verify(paymentService).initailPayment(any(PaymentRequest.class));
    }
}
