package com.travixa.dto.user;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean enabled,
        Set<String> roles,
        Instant dateCreated,
        Instant lastUpdated) {
}
