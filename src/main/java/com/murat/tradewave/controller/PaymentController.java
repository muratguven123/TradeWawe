package com.murat.tradewave.controller;

import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        List<PaymentResponse> history = paymentService.paymentGetHistory(email);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/send")
    public ResponseEntity<PaymentResponse> initialPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        PaymentResponse resp = paymentService.initailPayment(request);
        return ResponseEntity.ok(resp);
    }

}
