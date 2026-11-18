
package com.travixa.dto.payment;

import com.travixa.enums.RefundStatus;
import jakarta.validation.constraints.NotNull;

public record RefundReviewRequest(
        @NotNull RefundStatus status
) {
}
