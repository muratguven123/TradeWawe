package com.murat.tradewave.dto.store;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequest {
    @NotBlank @Size(min = 3, max = 60)
    public String name;
    @NotBlank @Pattern(regexp = "^[a-z0-9-]{3,60}$")
    public String slug;
    @Size(max = 2000)
    public String description;
    @Email
    public String email;
    public String phone;
    @Valid
    public AddressRequest address;
}


