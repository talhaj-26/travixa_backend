package com.travixa.security;

import com.travixa.enums.PartnerOrganizationStatus;
import com.travixa.exceptions.PartnerDataIsolationException;
import com.travixa.repository.partner.PartnerMembershipRepository;
import org.springframework.stereotype.Component;

@Component("partnerAccessEvaluator")
public class PartnerAccessEvaluator {

    private final PartnerMembershipRepository membershipRepository;

    public PartnerAccessEvaluator(PartnerMembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public boolean canAccessOrganization(String username, Long organizationId) {
        if (username == null || username.isBlank() || organizationId == null) {
            return false;
        }
        return membershipRepository.existsActiveMembership(
                username, organizationId, PartnerOrganizationStatus.ACTIVE);
    }

    public void requireOrganizationAccess(String username, Long organizationId) {
        if (!canAccessOrganization(username, organizationId)) {
            throw new PartnerDataIsolationException("You do not have access to this partner organization");
        }
    }
}
