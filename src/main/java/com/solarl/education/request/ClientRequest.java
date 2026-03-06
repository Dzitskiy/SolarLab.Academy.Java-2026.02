package com.solarl.education.request;

import com.solarl.education.validation.CapitalLetter;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    @NotBlank(message = "Наименование должно быть передано")
    @CapitalLetter
    private String name;
    @NotBlank
    private String email;
}
