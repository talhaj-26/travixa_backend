package com.travixa.dto.hotel;

import java.math.BigDecimal;

public record DestinationResponse(
        Long id,
        String name,
        String slug,
        String countryCode,
        String region,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        String category,
        String imageUrl,
        boolean featured,
        boolean published
) {
}