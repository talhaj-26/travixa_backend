package com.travixa.dto.auth;

import java.util.List;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String refreshToken,
        String username,
        String email,
        List<String> roles) {
}
