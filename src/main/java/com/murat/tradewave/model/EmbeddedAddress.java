package com.murat.tradewave.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmbeddedAddress {
    private String title;
    private String street;
    private String city;
    private String district;
    private String postalCode;
    private String country;
}

