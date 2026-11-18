package com.travixa.dto.payment;

import com.travixa.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PaymentInitiateRequest(
        @NotNull @Positive Long bookingId,
        @NotNull PaymentMethod method,
        @NotBlank @Size(max = 100) String idempotencyKey) {
}
