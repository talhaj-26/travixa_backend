
package com.travixa.dto.hotel;

import com.travixa.enums.BedType;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record RoomTypeRequest(
        @NotNull @Positive Long hotelId,
        @NotBlank @Size(max = 100) String name,
        @NotNull RoomType category,
        @NotNull BedType bedType,
        @Positive int maxOccupancy,
        @Positive BigDecimal sizeSquareMeters,
        @NotNull Boolean airConditioned,
        @Size(max = 2000) String description,
        @NotNull ListingStatus listingStatus,
        @Size(max = 30) List<@NotBlank @Size(max = 80) String> amenities
) {
}
