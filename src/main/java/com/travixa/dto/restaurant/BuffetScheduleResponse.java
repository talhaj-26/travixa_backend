package com.travixa.dto.restaurant;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BuffetScheduleResponse(
        Long id,
        Long restaurantId,
        LocalDate buffetDate,
        LocalTime startsAt,
        LocalTime endsAt,
        BigDecimal adultPrice,
        BigDecimal childPrice,
        CurrencyCode currency,
        int capacity,
        String menuSummary,
        ListingStatus listingStatus
) {
}
