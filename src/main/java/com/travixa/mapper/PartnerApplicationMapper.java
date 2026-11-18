package com.travixa.mapper;

import com.travixa.dto.partner.PartnerApplicationRequest;
import com.travixa.dto.partner.PartnerApplicationResponse;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerApplication;

import java.util.Objects;

public final class PartnerApplicationMapper {

    private PartnerApplicationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PartnerApplication toEntity(PartnerApplicationRequest request, User applicant) {
        PartnerApplication application = new PartnerApplication();
        application.setApplicant(Objects.requireNonNull(applicant, "applicant must not be null"));
        application.setBusinessType(request.businessType());
        application.setBusinessName(request.businessName());
        application.setLegalName(request.legalName());
        application.setRegistrationNumber(request.registrationNumber());
        application.setContactEmail(request.contactEmail());
        application.setContactPhone(request.contactPhone());
        return application;
    }

    public static PartnerApplicationResponse toResponse(PartnerApplication application) {
        return new PartnerApplicationResponse(application.getId(), application.getApplicant().getUserName(),
                application.getOrganization() == null ? null : application.getOrganization().getId(),
                application.getBusinessType(), application.getBusinessName(), application.getLegalName(),
                application.getRegistrationNumber(), application.getContactEmail(), application.getContactPhone(),
                application.getStatus(), application.getSubmittedAt());
    }
}
