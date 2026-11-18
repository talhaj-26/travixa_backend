package com.travixa.dto.booking;

import com.travixa.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        @NotNull @Positive Long bookingId,
        @NotNull PaymentMethod paymentMethod,
        @NotBlank @Size(max = 100) String idempotencyKey) {
}
