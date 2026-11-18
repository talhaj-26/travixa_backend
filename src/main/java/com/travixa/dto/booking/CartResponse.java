package com.travixa.dto.booking;

import com.travixa.enums.CartStatus;

import java.time.Instant;

public record CartResponse(Long id, String username, CartStatus status, Instant expiresAt) {
}
