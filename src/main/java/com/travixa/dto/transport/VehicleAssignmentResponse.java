package com.travixa.dto.transport;

import java.time.Instant;

public record VehicleAssignmentResponse(
        Long id,
        Long vehicleId,
        Long driverProfileId,
        Instant startsAt,
        Instant endsAt) {
}
