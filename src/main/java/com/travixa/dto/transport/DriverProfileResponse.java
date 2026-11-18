package com.travixa.dto.transport;

import com.travixa.enums.DriverStatus;

public record DriverProfileResponse(
        Long id,
        String userName,
        Long organizationId,
        String organizationName,
        String licenseReference,
        DriverStatus status) {
}
