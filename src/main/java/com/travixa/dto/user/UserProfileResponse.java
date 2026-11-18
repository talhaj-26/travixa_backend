package com.travixa.dto.user;

public record UserProfileResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        String email) {
}
