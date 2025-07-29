package com.murat.tradewave.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="order_items")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private Integer quantity;

    private BigDecimal price;

    @ManyToOne
    private Order order;
}
