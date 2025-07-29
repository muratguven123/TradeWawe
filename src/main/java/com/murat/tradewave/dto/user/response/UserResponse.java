package com.murat.tradewave.dto.user.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String token;
}
