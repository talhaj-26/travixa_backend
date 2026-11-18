package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(
        Long id,
        Long paymentId,
        String referenceNumber,
        BigDecimal amount,
        CurrencyCode currency,
        RefundStatus status,
        String reason,
        Instant processedAt) {
}
