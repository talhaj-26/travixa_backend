package com.travixa.mapper;

import com.travixa.dto.hotel.DestinationRequest;
import com.travixa.dto.hotel.DestinationResponse;
import com.travixa.entity.hotel_stays.Destination;

import java.util.Locale;

public final class DestinationMapper {

    private DestinationMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Destination toEntity(DestinationRequest request) {
        Destination destination = new Destination();
        updateEntity(destination, request);
        return destination;
    }

    public static void updateEntity(
            Destination destination,
            DestinationRequest request
    ) {
        destination.setName(request.name().trim());
        destination.setSlug(request.slug().trim().toLowerCase(Locale.ROOT));
        destination.setCountryCode(request.countryCode().trim().toUpperCase(Locale.ROOT));
        destination.setRegion(request.region());
        destination.setDescription(request.description());
        destination.setLatitude(request.latitude());
        destination.setLongitude(request.longitude());
        destination.setCategory(request.category());
        destination.setImageUrl(request.imageUrl());
        destination.setFeatured(request.featured());
        destination.setPublished(request.published());
    }

    public static DestinationResponse toResponse(Destination destination) {
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getSlug(),
                destination.getCountryCode(),
                destination.getRegion(),
                destination.getDescription(),
                destination.getLatitude(),
                destination.getLongitude(),
                destination.getCategory(),
                destination.getImageUrl(),
                destination.isFeatured(),
                destination.isPublished()
        );
    }
}