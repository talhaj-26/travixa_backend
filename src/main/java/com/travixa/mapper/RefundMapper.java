package com.travixa.mapper;

import com.travixa.dto.payment.RefundResponse;
import com.travixa.entity.payment_financial.RefundRequest;

public final class RefundMapper {

    private RefundMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RefundResponse toResponse(RefundRequest refund) {
        return new RefundResponse(refund.getId(), refund.getPayment().getId(), refund.getReferenceNumber(),
                refund.getAmount(), refund.getCurrency(), refund.getStatus(), refund.getReason(),
                refund.getProcessedAt());
    }
}
