package com.travixa.mapper;

import com.travixa.dto.hotel.HotelRequest;
import com.travixa.dto.hotel.HotelResponse;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.partner_management.PartnerOrganization;

import java.util.Objects;

public final class HotelMapper {

    private HotelMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Hotel toEntity(HotelRequest request, PartnerOrganization organization, Destination destination) {
        Hotel hotel = new Hotel();
        hotel.setOrganization(Objects.requireNonNull(organization, "organization must not be null"));
        hotel.setDestination(Objects.requireNonNull(destination, "destination must not be null"));
        updateEntity(hotel, request);
        return hotel;
    }

    public static void updateEntity(Hotel hotel, HotelRequest request) {
        hotel.setName(request.name());
        hotel.setSlug(request.slug());
        hotel.setDescription(request.description());
        hotel.setAddress(request.address());
        hotel.setContactEmail(request.contactEmail());
        hotel.setContactPhone(request.contactPhone());
        hotel.setLatitude(request.latitude());
        hotel.setLongitude(request.longitude());
        hotel.setStarRating(request.starRating());
        hotel.setListingStatus(request.listingStatus());
        hotel.setCheckInTime(request.checkInTime());
        hotel.setCheckOutTime(request.checkOutTime());
        hotel.setCancellationPolicy(request.cancellationPolicy());
        hotel.setChildPolicy(request.childPolicy());
        hotel.setAdditionalCharges(request.additionalCharges());
    }

    public static HotelResponse toResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getOrganization().getId(),
                hotel.getDestination().getId(),
                hotel.getDestination().getName(),
                hotel.getName(),
                hotel.getSlug(),
                hotel.getDescription(),
                hotel.getAddress(),
                hotel.getContactEmail(),
                hotel.getContactPhone(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                hotel.getStarRating(),
                hotel.getListingStatus(),
                hotel.getCheckInTime(),
                hotel.getCheckOutTime(),
                hotel.getCancellationPolicy(),
                hotel.getChildPolicy(),
                hotel.getAdditionalCharges()
        );
    }
}
