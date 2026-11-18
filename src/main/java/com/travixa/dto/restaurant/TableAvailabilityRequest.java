package com.travixa.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalTime;

public record TableAvailabilityRequest(
        @NotNull @Positive Long restaurantId,
        @NotNull @Positive Long tableId,
        @NotNull LocalDate availableOn,
        @NotNull LocalTime startsAt,
        @NotNull LocalTime endsAt,
        @PositiveOrZero int availableSeats) {
}
