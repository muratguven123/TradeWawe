package com.murat.tradewave.dto.Seller;

import com.murat.tradewave.Enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class SellerResponse {
        private Long id;
        private String name;
        private String email;
        private OrderStatus status;
        private Instant createdAt;
}
