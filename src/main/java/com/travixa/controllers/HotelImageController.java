package com.travixa.controllers;

import com.travixa.dto.hotel.HotelImageResponse;
import com.travixa.enums.ListingStatus;
import com.travixa.repository.hotel.HotelImageRepository;
import com.travixa.repository.hotel.HotelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelImageController {
    private final HotelRepository hotels;
    private final HotelImageRepository images;

    public HotelImageController(HotelRepository hotels, HotelImageRepository images) {
        this.hotels = hotels;
        this.images = images;
    }

    @GetMapping("/{hotelId}/images")
    @Transactional(readOnly = true)
    public List<HotelImageResponse> publishedImages(@PathVariable Long hotelId) {
        if (hotels.findByIdAndListingStatus(hotelId, ListingStatus.PUBLISHED).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published hotel not found");
        }
        return images.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(hotelId).stream()
                .map(image -> new HotelImageResponse(image.getId(), hotelId, image.getStorageKey(),
                        "/api/media/" + image.getStorageKey(), image.getAltText(), image.getDisplayOrder(), image.isCoverImage()))
                .toList();
    }

    @GetMapping("/admin/{hotelId}/images")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<HotelImageResponse> adminImages(@PathVariable Long hotelId) {
        if (!hotels.existsById(hotelId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        return images.findAllByHotel_IdOrderByDisplayOrderAscIdAsc(hotelId).stream()
                .map(image -> new HotelImageResponse(image.getId(), hotelId, image.getStorageKey(),
                        "/api/media/" + image.getStorageKey(), image.getAltText(), image.getDisplayOrder(), image.isCoverImage()))
                .toList();
    }
}
