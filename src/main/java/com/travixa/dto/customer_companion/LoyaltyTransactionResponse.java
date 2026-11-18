package com.travixa.dto.customer_companion;

import com.travixa.enums.LoyaltyTransactionType;

public record LoyaltyTransactionResponse(
        Long id,
        LoyaltyTransactionType type,
        long points,
        String referenceNumber,
        String description) {
}
