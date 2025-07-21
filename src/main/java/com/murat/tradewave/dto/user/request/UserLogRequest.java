package com.murat.tradewave.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLogRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid Email Format")
    private String email;
    @NotBlank(message = "Password must not be blank")
    private String password;
}
