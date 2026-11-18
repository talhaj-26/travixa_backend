package com.travixa.dto.payment;

import com.travixa.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PaymentInitiationRequest(
        @NotNull @Positive Long bookingId,
        @NotNull PaymentMethod method,
        @NotBlank @Size(max = 128) String idempotencyKey
) {
}
