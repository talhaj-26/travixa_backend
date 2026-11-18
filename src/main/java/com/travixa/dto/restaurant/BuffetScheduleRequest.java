package com.travixa.dto.restaurant;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BuffetScheduleRequest(
        @NotNull @Positive Long restaurantId,
        @NotNull LocalDate buffetDate,
        @NotNull LocalTime startsAt,
        @NotNull LocalTime endsAt,
        @NotNull @Positive BigDecimal adultPrice,
        @PositiveOrZero BigDecimal childPrice,
        @NotNull CurrencyCode currency,
        @Positive int capacity,
        @Size(max = 2000) String menuSummary,
        @NotNull ListingStatus listingStatus
) {
}
