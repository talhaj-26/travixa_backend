package com.travixa.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record HotelGalleryRequest(
        @NotNull @Positive Long hotelId,
        @NotBlank @Size(max = 512) String storageKey,
        @Size(max = 300) String altText,
        @PositiveOrZero int displayOrder,
        boolean coverImage) {
}
