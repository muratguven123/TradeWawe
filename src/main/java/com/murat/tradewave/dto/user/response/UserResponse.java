package com.murat.tradewave.dto.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String token;
}
