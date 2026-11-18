package com.travixa.dto.partner;

import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerOrganizationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnerOrganizationRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 180) String slug,
        @NotNull PartnerBusinessType businessType,
        @Size(max = 180) String legalName,
        @Size(max = 100) String registrationNumber,
        @NotBlank @Email @Size(max = 254) String contactEmail,
        @Size(max = 32) String contactPhone,
        @NotNull PartnerOrganizationStatus status) {
}
