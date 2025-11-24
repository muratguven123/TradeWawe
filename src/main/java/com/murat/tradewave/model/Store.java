package com.murat.tradewave.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeid;

    private String storeName;
    private String location;
    private String storephone;
    private String storeemail;
    private Long averageResponseTime;
    private Long averageDeliveryTime;

    @Column(length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
}
