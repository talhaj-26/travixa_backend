package com.travixa.dto.media;

public record MediaUploadResponse(
        Long id,
        String listingType,
        Long listingId,
        String storageKey,
        String publicUrl,
        String altText,
        int displayOrder,
        boolean coverImage) {
}
