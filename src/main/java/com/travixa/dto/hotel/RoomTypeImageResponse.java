package com.travixa.dto.hotel;

public record RoomTypeImageResponse(
        Long id,
        Long roomTypeId,
        String storageKey,
        String publicUrl,
        String altText,
        int displayOrder,
        boolean coverImage
) { }
