package com.travixa.dto.partner;

import com.travixa.enums.PartnerApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartnerApplicationReviewRequest(
        @NotNull PartnerApplicationStatus decision,
        @Size(max = 2000) String notes) {
}
