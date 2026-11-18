package com.travixa.dto.transport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportStopRequest(
        @NotNull @Positive Long routeId,
        @Positive Long destinationId,
        @Positive int stopSequence,
        @NotBlank @Size(max = 180) String stopName,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant plannedArrivalAt) {
}
