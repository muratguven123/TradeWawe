package com.murat.tradewave.dto.user.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserLogResponse {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid Email Format")
    private String email;
    @NotBlank(message = "Password must not be blank")
    private String password;
}
