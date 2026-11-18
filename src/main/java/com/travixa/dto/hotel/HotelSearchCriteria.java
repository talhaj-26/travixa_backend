package com.travixa.dto.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record HotelSearchCriteria(
        @Positive Long destinationId,
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut,
        @Positive int guests,
        @Max(5) Integer minimumStarRating) {
}
