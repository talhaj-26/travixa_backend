package com.travixa.dto.partner;

import com.travixa.enums.PartnerApplicationStatus;

import java.time.Instant;

public record PartnerApprovalResponse(
        Long id,
        Long applicationId,
        String reviewerUsername,
        PartnerApplicationStatus decision,
        String notes,
        Instant decidedAt) {
}
