package com.travixa.dto.support_platform;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactInquiryRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 40) String phoneNumber,
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(max = 10000) String message) {
}
