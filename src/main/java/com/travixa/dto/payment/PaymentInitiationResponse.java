
package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.PaymentMethod;
import com.travixa.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentInitiationResponse(
        Long paymentId,
        Long bookingId,
        String referenceNumber,
        BigDecimal amount,
        CurrencyCode currency,
        PaymentMethod method,
        PaymentStatus status,
        boolean gatewayConfigured,
        boolean demoMode,
        String message
) {
}
