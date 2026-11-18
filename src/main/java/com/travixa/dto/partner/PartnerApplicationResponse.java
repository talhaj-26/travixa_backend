package com.travixa.dto.partner;

import com.travixa.enums.PartnerApplicationStatus;
import com.travixa.enums.PartnerBusinessType;

import java.time.Instant;

public record PartnerApplicationResponse(
        Long id,
        String applicantId,
        Long organizationId,
        PartnerBusinessType businessType,
        String businessName,
        String legalName,
        String registrationNumber,
        String contactEmail,
        String contactPhone,
        PartnerApplicationStatus status,
        Instant submittedAt) {
}
