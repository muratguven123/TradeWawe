package com.murat.tradewave.dto.user.response;

import lombok.*;

@Getter@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String token;
}
