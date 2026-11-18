package com.travixa.dto.customer_companion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmergencyContactRequest(
        @NotBlank @Size(max = 150) String contactName,
        @Size(max = 80) String relationship,
        @NotBlank @Size(max = 40) String phoneNumber,
        @Email @Size(max = 254) String email,
        boolean primaryContact) {
}
