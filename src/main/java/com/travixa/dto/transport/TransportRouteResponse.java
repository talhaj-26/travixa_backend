package com.travixa.dto.transport;

import com.travixa.enums.RouteStatus;

import java.math.BigDecimal;

public record TransportRouteResponse(
        Long id,
        Long organizationId,
        Long originDestinationId,
        Long destinationId,
        String originName,
        String destinationName,
        BigDecimal distanceKm,
        RouteStatus status) {
}
