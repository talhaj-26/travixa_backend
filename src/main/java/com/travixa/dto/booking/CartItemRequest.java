package com.travixa.dto.booking;

import com.travixa.enums.BookingType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CartItemRequest(
        @NotNull BookingType resourceType,
        @NotNull @Positive Long resourceId,
        @Positive int quantity,
        @Size(max = 1000) String optionsJson
) {
}