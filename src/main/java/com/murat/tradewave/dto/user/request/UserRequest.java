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
@Email(message = "Invalid Email Format")
    private String email;
    private String password;
    private String name;
    private Role role;

}
