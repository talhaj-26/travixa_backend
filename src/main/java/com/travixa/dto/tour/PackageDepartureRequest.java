package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;

public record PackageDepartureRequest(
        @NotNull @Positive Long tourPackageId,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        @Positive int seatCapacity,
        @PositiveOrZero BigDecimal priceOverride,
        @NotNull CurrencyCode currency,
        @NotNull ScheduleStatus status) {
}
