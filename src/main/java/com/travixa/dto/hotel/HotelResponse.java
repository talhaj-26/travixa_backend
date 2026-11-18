
package com.travixa.dto.hotel;

import com.travixa.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalTime;

public record HotelResponse(
        Long id,
        Long organizationId,
        Long destinationId,
        String destinationName,
        String name,
        String slug,
        String description,
        String address,
        String contactEmail,
        String contactPhone,
        BigDecimal latitude,
        BigDecimal longitude,
        int starRating,
        ListingStatus listingStatus,
        LocalTime checkInTime,
        LocalTime checkOutTime,
        String cancellationPolicy,
        String childPolicy,
        String additionalCharges
) {
}
