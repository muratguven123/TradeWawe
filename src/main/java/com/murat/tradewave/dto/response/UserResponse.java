package com.murat.tradewave.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String token;
}
