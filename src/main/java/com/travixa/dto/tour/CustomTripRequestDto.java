package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomTripRequestDto(
        @NotNull @Positive Long destinationId,
        @Positive int travelerCount,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @PositiveOrZero BigDecimal targetBudget,
        @NotNull CurrencyCode currency,
        @Size(max = 4000) String requirements) {
}
