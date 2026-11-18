package com.travixa.dto.partner;

import com.travixa.enums.PartnerBusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnerApplicationRequest(
        @NotNull PartnerBusinessType businessType,
        @NotBlank @Size(max = 180) String businessName,
        @Size(max = 180) String legalName,
        @Size(max = 100) String registrationNumber,
        @NotBlank @Email @Size(max = 254) String contactEmail,
        @NotBlank @Size(max = 40) String contactPhone) {
}
