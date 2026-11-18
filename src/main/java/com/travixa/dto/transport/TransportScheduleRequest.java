package com.travixa.dto.transport;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportScheduleRequest(
        @NotNull @Positive Long organizationId,
        @NotNull @Positive Long routeId,
        @Positive Long vehicleId,
        @NotNull Instant departureAt,
        @NotNull Instant arrivalAt,
        @NotNull @Positive BigDecimal ticketPrice,
        @NotNull CurrencyCode currency,
        @NotNull ScheduleStatus status) {
}
