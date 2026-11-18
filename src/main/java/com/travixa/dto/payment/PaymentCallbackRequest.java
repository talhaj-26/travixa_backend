package com.travixa.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentCallbackRequest(
        @NotBlank @Size(max = 200) String providerReference,
        @Size(max = 100) String providerResultCode,
        @Size(max = 2000) String providerMessage) {
}
