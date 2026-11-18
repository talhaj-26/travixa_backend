package com.travixa.dto.transport;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ScheduleStatus;
import com.travixa.enums.VehicleType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportScheduleResponse(
        Long id,
        Long organizationId,
        Long routeId,
        String originName,
        String destinationName,
        Long vehicleId,
        VehicleType vehicleType,
        String providerName,
        String vehicleLabel,
        Instant departureAt,
        Instant arrivalAt,
        BigDecimal ticketPrice,
        CurrencyCode currency,
        ScheduleStatus status,
        int seatCapacity,
        long availableSeats) {
}
