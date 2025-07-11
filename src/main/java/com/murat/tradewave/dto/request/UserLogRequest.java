package com.murat.tradewave.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter @Setter
public class UserLogRequest {
    private String email;
    private String password;
}
