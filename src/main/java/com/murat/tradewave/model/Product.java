package com.murat.tradewave.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="products")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length=1000)
    private String description;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
