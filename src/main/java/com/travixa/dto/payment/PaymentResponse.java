package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.PaymentMethod;
import com.travixa.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long bookingId,
        String referenceNumber,
        BigDecimal amount,
        CurrencyCode currency,
        PaymentMethod method,
        PaymentStatus status,
        Instant paidAt) {
}