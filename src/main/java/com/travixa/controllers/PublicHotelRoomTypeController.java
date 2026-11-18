
package com.travixa.controllers;

import com.travixa.dto.hotel.HotelRoomTypeResponse;
import com.travixa.dto.hotel.RoomTypeImageResponse;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.RoomRate;
import com.travixa.entity.hotel_stays.RoomType;
import com.travixa.enums.ListingStatus;
import com.travixa.services.hotel.HotelInventoryService;
import com.travixa.services.hotel.HotelService;
import com.travixa.repository.hotel.RoomTypeImageRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels/{hotelId}/room-types")
@PreAuthorize("permitAll()")
public class PublicHotelRoomTypeController {

    private final HotelService hotelService;
    private final HotelInventoryService hotelInventoryService;
    private final RoomTypeImageRepository roomTypeImages;

    public PublicHotelRoomTypeController(
            HotelService hotelService,
            HotelInventoryService hotelInventoryService,
            RoomTypeImageRepository roomTypeImages
    ) {
        this.hotelService = hotelService;
        this.hotelInventoryService = hotelInventoryService;
        this.roomTypeImages = roomTypeImages;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<HotelRoomTypeResponse> findPublishedRoomTypes(
            @PathVariable Long hotelId
    ) {
        Hotel hotel = hotelService.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"
                ));

        if (hotel.getListingStatus() != ListingStatus.PUBLISHED) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Hotel not found"
            );
        }

        return hotelInventoryService
                .findPublishedRoomTypesByHotelId(hotelId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HotelRoomTypeResponse toResponse(RoomType roomType) {
        Optional<RoomRate> currentRate =
                hotelInventoryService.findCurrentRateForRoomType(
                        roomType.getId()
                );

        return currentRate
                .map(rate -> new HotelRoomTypeResponse(
                        roomType.getId(),
                        roomType.getName(),
                        roomType.getCategory(),
                        roomType.getBedType(),
                        roomType.getMaxOccupancy(),
                        roomType.getSizeSquareMeters(),
                        roomType.getAirConditioned(),
                        roomType.getDescription(),
                        roomType.getAmenities() == null ? List.of() : List.copyOf(roomType.getAmenities()),
                        images(roomType),
                        rate.getNightlyRate(),
                        rate.getCurrency(),
                        rate.getRatePlanType()
                ))
                .orElseGet(() -> new HotelRoomTypeResponse(
                        roomType.getId(),
                        roomType.getName(),
                        roomType.getCategory(),
                        roomType.getBedType(),
                        roomType.getMaxOccupancy(),
                        roomType.getSizeSquareMeters(),
                        roomType.getAirConditioned(),
                        roomType.getDescription(),
                        roomType.getAmenities() == null ? List.of() : List.copyOf(roomType.getAmenities()),
                        images(roomType),
                        null,
                        null,
                        null
                ));
    }

    private List<RoomTypeImageResponse> images(RoomType roomType) {
        return roomTypeImages.findAllByRoomType_IdOrderByDisplayOrderAscIdAsc(roomType.getId())
                .stream()
                .map(image -> new RoomTypeImageResponse(image.getId(), roomType.getId(), image.getStorageKey(),
                        "/api/media/" + image.getStorageKey(), image.getAltText(), image.getDisplayOrder(),
                        image.isCoverImage()))
                .toList();
    }
}
