package com.murat.tradewave.model;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.Enums.Role;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "seller")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Seller {
    @jakarta.persistence.Id
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Role accountType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant approvedAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

    @ManyToOne
    private BankAccount bankAccount;

    private String email;
    @NotNull
    private String password;

    @NotBlank
    private String name;

}
