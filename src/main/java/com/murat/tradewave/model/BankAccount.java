package com.murat.tradewave.model;

import com.murat.tradewave.Enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "bank_account")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private Seller seller;

    @NotBlank
    @Size(max = 34)
    @Column(length = 34, unique = true)
    private String iban;

    @NotBlank
    @Size(max = 120)
    @Column(length = 120)
    private String accountHolderName;

    @Size(max = 120)
    @Column(length = 120)
    private String bankName;

    @Size(max = 3)
    @Column(length = 3)
    private String currency = "TRY";

    @Enumerated(EnumType.STRING)
    private PaymentStatus payoutSchedule = PaymentStatus.Pending;

    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
