package com.travixa.dto.hotel;

public record HotelImageResponse(Long id, Long hotelId, String storageKey, String publicUrl,
                                 String altText, int displayOrder, boolean coverImage) { }
