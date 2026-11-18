package com.travixa.dto.transport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record VehicleAssignmentRequest(
        @NotNull @Positive Long vehicleId,
        @NotNull @Positive Long driverProfileId,
        @NotNull Instant startsAt,
        Instant endsAt) {
}
