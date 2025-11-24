package com.murat.tradewave.dto.payment.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull
    private Long orderId;
    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;
    @NotBlank
    private String status;
    @NotNull
    private LocalDateTime paidAt;
}

