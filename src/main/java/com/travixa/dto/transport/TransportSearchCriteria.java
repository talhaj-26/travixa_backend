package com.travixa.dto.transport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record TransportSearchCriteria(
        @Positive Long originDestinationId,
        @Positive Long destinationId,
        @NotNull LocalDate departureDate,
        @Positive int passengers) {
}
