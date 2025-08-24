package com.murat.tradewave.model;

import com.murat.tradewave.Enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

import java.time.Instant;
@Entity
@Table(name = "BankAccount")
public class BankAccount {
    @jakarta.persistence.Id
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", unique = true)
    private Seller seller;

    @NotBlank
    @Size(max = 34)
    private String iban;

    @NotBlank @Size(max = 120)
    private String accountHolderName;

    @Size(max = 120)
    private String bankName;

    @Size(max = 3)
    private String currency = "TRY";

    @Enumerated(EnumType.STRING)
    private PaymentStatus payoutSchedule = PaymentStatus.Pending;

    private Instant createdAt;

}
