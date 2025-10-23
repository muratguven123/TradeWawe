package com.murat.tradewave.dto.user.request;

import com.murat.tradewave.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid Email Format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Name must not be blank")
    private String name;

    private Role role;

}
