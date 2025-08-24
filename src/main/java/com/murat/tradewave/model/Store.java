package com.murat.tradewave.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Store")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long storeid;
    private String storeName;
    private String location;
    private String storephone;
    private String storeemail;
    private Long averageResponseTime;
    private Long averageDeliveryTime;
    @Column(length=200)
    private String description;

}
