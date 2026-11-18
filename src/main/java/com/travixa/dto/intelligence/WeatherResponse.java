package com.travixa.dto.intelligence;

import java.math.BigDecimal;
import java.time.Instant;

public record WeatherResponse(
        Long id,
        Long destinationId,
        String locationName,
        BigDecimal temperatureCelsius,
        String conditionCode,
        Instant observedAt,
        Instant expiresAt) {
}
