package com.travixa.dto.transport;

import com.travixa.enums.DriverStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DriverProfileRequest(
        @NotBlank @Size(max = 100) String userName,
        @NotNull @Positive Long organizationId,
        @Size(max = 80) String licenseReference,
        @NotNull DriverStatus status) {
}
