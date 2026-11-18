package com.travixa.dto.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record RoomAvailabilityRequest(
        @NotNull @Positive Long roomId,
        @NotNull LocalDate availableOn,
        @PositiveOrZero @Max(1) int availableUnits) {
}
