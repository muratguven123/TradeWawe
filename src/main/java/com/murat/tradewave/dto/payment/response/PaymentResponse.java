package com.murat.tradewave.dto.payment.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
