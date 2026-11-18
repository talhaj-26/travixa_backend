package com.travixa.dto.hotel;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RatePlanType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RoomRateResponse(
        Long id,
        Long roomTypeId,
        RatePlanType ratePlanType,
        BigDecimal nightlyRate,
        CurrencyCode currency,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        boolean active) {
}
