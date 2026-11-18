package com.travixa.dto.restaurant;

import com.travixa.enums.RestaurantReservationStatus;

import java.time.Instant;

public record RestaurantReservationResponse(
        Long id,
        Long restaurantId,
        Long tableId,
        Instant startsAt,
        int guestCount,
        RestaurantReservationStatus status,
        String specialRequests) {
}
