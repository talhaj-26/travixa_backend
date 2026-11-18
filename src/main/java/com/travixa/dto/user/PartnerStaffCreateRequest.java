package com.travixa.dto.user;

import com.travixa.enums.PartnerMembershipRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PartnerStaffCreateRequest(
        @NotNull @Positive Long userId,
        @NotNull @Positive Long organizationId,
        @NotNull PartnerMembershipRole membershipRole) {
}
