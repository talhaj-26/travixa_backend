
package com.travixa.dto.hotel;

import com.travixa.enums.BedType;
import com.travixa.enums.ListingStatus;

import java.math.BigDecimal;
import java.util.List;

public record RoomTypeResponse(
        Long id,
        Long hotelId,
        String name,
        com.travixa.enums.RoomType category,
        BedType bedType,
        int maxOccupancy,
        BigDecimal sizeSquareMeters,
        Boolean airConditioned,
        String description,
        List<String> amenities,
        ListingStatus listingStatus
) {
}
