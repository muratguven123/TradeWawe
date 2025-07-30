package com.murat.tradewave.dto.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCreateRequest {
    private String text;
    private String subject;
}
