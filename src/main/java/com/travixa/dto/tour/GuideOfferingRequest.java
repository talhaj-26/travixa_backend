package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record GuideOfferingRequest(
        @NotNull @Positive Long guideProfileId,
        @NotBlank @Size(max = 140) String name,
        @Size(max = 2000) String description,
        @NotNull @Positive BigDecimal pricePerHour,
        @NotNull CurrencyCode currency,
        boolean active) {
}
