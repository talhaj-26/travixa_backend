package com.travixa.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetTokenValidationRequest(
        @NotBlank @Size(max = 512) String resetToken) {
}
