package com.travixa.dto.booking;

import com.travixa.enums.BookingType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingItemRequest(
        @NotNull BookingType resourceType,
        @NotNull @Positive Long resourceId,
        @Positive int quantity,
        @Positive Long departureId) {
}
