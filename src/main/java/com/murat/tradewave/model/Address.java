package com.murat.tradewave.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "address")
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String title;
    private String street;
    private String city;
    private String discrict;
    private String postalCode;
    private String country;
    private boolean isDefault;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}