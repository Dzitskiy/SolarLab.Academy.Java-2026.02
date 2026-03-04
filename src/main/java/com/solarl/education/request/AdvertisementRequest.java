package com.solarl.education.request;

import com.solarl.education.validation.CapitalLetter;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdvertisementRequest {
    @NotBlank
    @CapitalLetter
    private String name;
    private String category;
    private String subcategory;
    @Positive
    private Integer cost;
    private String address;
    @CapitalLetter
    private String description;
    @NotNull
    @PastOrPresent
    private LocalDateTime createDateTime;
}
