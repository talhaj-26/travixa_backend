package com.travixa.mapper;

import com.travixa.dto.partner.PartnerOrganizationRequest;
import com.travixa.dto.partner.PartnerOrganizationResponse;
import com.travixa.entity.partner_management.PartnerOrganization;

public final class PartnerOrganizationMapper {

    private PartnerOrganizationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PartnerOrganization toEntity(PartnerOrganizationRequest request) {
        PartnerOrganization organization = new PartnerOrganization();
        updateEntity(organization, request);
        return organization;
    }

    public static void updateEntity(PartnerOrganization organization, PartnerOrganizationRequest request) {
        organization.setName(request.name());
        organization.setSlug(request.slug());
        organization.setBusinessType(request.businessType());
        organization.setLegalName(request.legalName());
        organization.setRegistrationNumber(request.registrationNumber());
        organization.setContactEmail(request.contactEmail());
        organization.setContactPhone(request.contactPhone());
        organization.setStatus(request.status());
    }

    public static PartnerOrganizationResponse toResponse(PartnerOrganization organization) {
        return new PartnerOrganizationResponse(organization.getId(), organization.getName(), organization.getSlug(),
                organization.getBusinessType(), organization.getLegalName(), organization.getRegistrationNumber(),
                organization.getContactEmail(), organization.getContactPhone(), organization.getStatus());
    }
}
