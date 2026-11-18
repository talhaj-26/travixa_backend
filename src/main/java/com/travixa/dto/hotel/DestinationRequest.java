package com.travixa.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DestinationRequest(
        @NotBlank @Size(max = 140) String name,
        @NotBlank @Size(max = 160)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,
        @NotBlank @Pattern(regexp = "^[A-Za-z]{2}$")
        String countryCode,
        @Size(max = 120) String region,
        @Size(max = 3000) String description,
        BigDecimal latitude,
        BigDecimal longitude,
        @Size(max = 50) String category,
        @Size(max = 1000) String imageUrl,
        boolean featured,
        boolean published
) {
}