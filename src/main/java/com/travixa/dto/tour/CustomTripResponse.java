package com.travixa.dto.tour;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.CustomTripRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomTripResponse(
        Long id,
        Long destinationId,
        int travelerCount,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal targetBudget,
        CurrencyCode currency,
        String requirements,
        CustomTripRequestStatus status) {
}
