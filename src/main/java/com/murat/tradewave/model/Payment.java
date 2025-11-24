package com.murat.tradewave.model;

import com.murat.tradewave.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    private Order order;
}
