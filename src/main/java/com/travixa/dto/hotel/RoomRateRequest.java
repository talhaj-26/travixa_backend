package com.travixa.dto.hotel;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RatePlanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RoomRateRequest(
        @NotNull @Positive Long roomTypeId,
        @NotNull RatePlanType ratePlanType,
        @NotNull @Positive BigDecimal nightlyRate,
        @NotNull CurrencyCode currency,
        @NotNull LocalDateTime validFrom,
        @NotNull LocalDateTime validUntil,
        boolean active) {
}
