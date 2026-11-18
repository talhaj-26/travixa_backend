package com.travixa.dto.transport;

import com.travixa.enums.VehicleStatus;
import com.travixa.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransportVehicleRequest(
        @NotNull @Positive Long organizationId,
        @NotNull VehicleType vehicleType,
        @NotBlank @Size(max = 32) String registrationNumber,
        @Size(max = 80) String make,
        @Size(max = 80) String modelName,
        @Positive int seatCapacity,
        @NotNull VehicleStatus status) {
}
