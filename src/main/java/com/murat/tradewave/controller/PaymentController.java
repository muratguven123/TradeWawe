package com.murat.tradewave.controller;

import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

private final PaymentService paymentService;


    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(Authentication authentication) {
        String email = authentication.getName();
        List<PaymentResponse> history = paymentService.paymentGetHistory(email);
        return ResponseEntity.ok(history);
    }


    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> initialPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // gerekiyorsa servise gönder
        PaymentResponse resp = paymentService.initailPayment(request /*, email */);
        return ResponseEntity.ok(resp);
    }

}
