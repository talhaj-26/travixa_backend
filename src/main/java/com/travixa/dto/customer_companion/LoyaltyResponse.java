package com.travixa.dto.customer_companion;

public record LoyaltyResponse(Long id, long pointsBalance, String tier, boolean active) {
}
