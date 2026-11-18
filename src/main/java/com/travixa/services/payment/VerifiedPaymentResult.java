
package com.travixa.services.payment;

import com.travixa.enums.PaymentStatus;

public record VerifiedPaymentResult(
        boolean verified,
        boolean successful,
        PaymentStatus status,
        String providerReference,
        String resultCode,
        String message
) {
}
