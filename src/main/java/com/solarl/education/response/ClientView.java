package com.solarl.education.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientView {
    private Long id;
    private String name;
    private String email;
}
