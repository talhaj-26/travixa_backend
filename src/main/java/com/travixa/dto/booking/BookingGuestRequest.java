package com.travixa.dto.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookingGuestRequest(
        @NotBlank @Size(max = 150) String fullName,
        @Email @Size(max = 254) String email,
        @Size(max = 40) String phoneNumber,
        boolean leadGuest) {
}
