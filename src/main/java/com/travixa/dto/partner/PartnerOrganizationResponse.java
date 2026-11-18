package com.travixa.dto.partner;

import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerOrganizationStatus;

public record PartnerOrganizationResponse(
        Long id,
        String name,
        String slug,
        PartnerBusinessType businessType,
        String legalName,
        String registrationNumber,
        String contactEmail,
        String contactPhone,
        PartnerOrganizationStatus status) {
}
