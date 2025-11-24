package com.murat.tradewave.model;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA no-arg
@AllArgsConstructor
@Builder(toBuilder = true)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role accountType;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant approvedAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    private String email;

    @NotNull
    private String password;

    @NotBlank
    private String name;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}