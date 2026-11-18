package com.travixa.dto.user;

import com.travixa.enums.PartnerMembershipRole;
import jakarta.validation.constraints.NotNull;

public record PartnerStaffUpdateRequest(
        PartnerMembershipRole membershipRole,
        @NotNull Boolean active) {
}
