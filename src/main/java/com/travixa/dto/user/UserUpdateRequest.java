package com.travixa.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 3, max = 100) String username,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Email @Size(max = 254) String email) {
}
