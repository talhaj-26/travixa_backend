package com.travixa.dto.transport;

import com.travixa.enums.RouteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransportRouteRequest(
        @NotNull @Positive Long organizationId,
        @Positive Long originDestinationId,
        @Positive Long destinationId,
        @NotBlank @Size(max = 180) String originName,
        @NotBlank @Size(max = 180) String destinationName,
        @Positive BigDecimal distanceKm,
        @NotNull RouteStatus status) {
}
