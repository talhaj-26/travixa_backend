package com.travixa.mapper;

import com.travixa.dto.partner.PartnerMembershipResponse;
import com.travixa.entity.partner_management.PartnerMembership;

public final class PartnerMembershipMapper {

    private PartnerMembershipMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static PartnerMembershipResponse toResponse(PartnerMembership membership) {
        return new PartnerMembershipResponse(membership.getId(), membership.getUser().getUserName(),
                membership.getOrganization().getId(), membership.getMembershipRole(), membership.isActive());
    }
}
