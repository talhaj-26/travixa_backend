
package com.travixa.mapper;

import com.travixa.dto.hotel.RoomTypeRequest;
import com.travixa.dto.hotel.RoomTypeResponse;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.RoomType;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public final class RoomTypeMapper {

    private RoomTypeMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static RoomType toEntity(
            RoomTypeRequest request,
            Hotel hotel
    ) {
        RoomType roomType = new RoomType();
        roomType.setHotel(
                Objects.requireNonNull(hotel, "hotel must not be null")
        );
        updateEntity(roomType, request);
        return roomType;
    }

    public static void updateEntity(
            RoomType roomType,
            RoomTypeRequest request
    ) {
        roomType.setName(request.name());
        roomType.setCategory(request.category());
        roomType.setBedType(request.bedType());
        roomType.setMaxOccupancy(request.maxOccupancy());
        roomType.setSizeSquareMeters(request.sizeSquareMeters());
        roomType.setAirConditioned(request.airConditioned());
        roomType.setDescription(request.description());
        List<String> amenities = request.amenities() == null
                ? List.of()
                : request.amenities().stream().map(String::trim).distinct().toList();
        if (roomType.getAmenities() == null) {
            roomType.setAmenities(new ArrayList<>());
        } else {
            roomType.getAmenities().clear();
        }
        roomType.getAmenities().addAll(amenities);
        roomType.setListingStatus(request.listingStatus());
    }

    public static RoomTypeResponse toResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getHotel().getId(),
                roomType.getName(),
                roomType.getCategory(),
                roomType.getBedType(),
                roomType.getMaxOccupancy(),
                roomType.getSizeSquareMeters(),
                roomType.getAirConditioned(),
                roomType.getDescription(),
                roomType.getAmenities() == null ? List.of() : List.copyOf(roomType.getAmenities()),
                roomType.getListingStatus()
        );
    }
}
