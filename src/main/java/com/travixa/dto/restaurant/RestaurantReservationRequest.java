package com.travixa.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.time.Instant;

public record RestaurantReservationRequest(
        @NotNull @Positive Long restaurantId,
        @Positive Long tableId,
        @NotNull @Future Instant startsAt,
        @Positive int guestCount,
        @Size(max = 1000) String specialRequests) {
}
