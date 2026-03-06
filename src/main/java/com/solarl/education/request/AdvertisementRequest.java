package com.solarl.education.request;

import com.solarl.education.enums.CategoryEnum;
import com.solarl.education.validation.CapitalLetter;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdvertisementRequest {
    @NotBlank
    @CapitalLetter
    private String name;
    private CategoryEnum category;
    private String subcategory;
    @Positive
    private Integer cost;
    private String address;
    private Long clientId;
    @CapitalLetter
    private String description;
    @NotNull
    @PastOrPresent
    private LocalDateTime createDateTime;
}
