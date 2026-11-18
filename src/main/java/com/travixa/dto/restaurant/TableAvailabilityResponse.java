package com.travixa.dto.restaurant;

import java.time.LocalDate;
import java.time.LocalTime;

public record TableAvailabilityResponse(
        Long id,
        Long restaurantId,
        Long tableId,
        String tableNumber,
        LocalDate availableOn,
        LocalTime startsAt,
        LocalTime endsAt,
        int availableSeats) {
}
