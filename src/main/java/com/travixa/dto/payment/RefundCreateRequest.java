
package com.travixa.dto.payment;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RefundCreateRequest(

        @NotNull
        @Positive
        Long paymentId,

        @NotNull
        @Positive
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @NotBlank
        @Size(max = 1000)
        String reason,

        @NotBlank
        @Size(max = 128)
        String idempotencyKey

) {
}
