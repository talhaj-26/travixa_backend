package com.travixa.dto.partner;

import com.travixa.enums.PartnerMembershipRole;

public record PartnerMembershipResponse(
        Long id,
        String userId,
        Long organizationId,
        PartnerMembershipRole membershipRole,
        boolean active) {
}
