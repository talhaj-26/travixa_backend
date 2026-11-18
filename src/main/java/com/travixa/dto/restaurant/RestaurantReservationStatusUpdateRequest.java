package com.travixa.dto.restaurant;

import com.travixa.enums.RestaurantReservationStatus;
import jakarta.validation.constraints.NotNull;

public record RestaurantReservationStatusUpdateRequest(
        @NotNull RestaurantReservationStatus status
) {
}
