package com.travixa.controllers;

import com.travixa.dto.hotel.HotelAmenityRequest;
import com.travixa.dto.hotel.HotelAmenityResponse;
import com.travixa.entity.hotel_stays.HotelAmenity;
import com.travixa.services.hotel.HotelAmenityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelAmenityController {
    private final HotelAmenityService service;

    public HotelAmenityController(HotelAmenityService service) {
        this.service = service;
    }

    @GetMapping("/{hotelId}/amenities")
    @Transactional(readOnly = true)
    public List<HotelAmenityResponse> published(@PathVariable Long hotelId) {
        return service.findPublished(hotelId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/admin/{hotelId}/amenities")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<HotelAmenityResponse> findForAdmin(@PathVariable Long hotelId) {
        return service.findForAdmin(hotelId).stream().map(this::toResponse).toList();
    }

    @PostMapping("/{hotelId}/amenities")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public HotelAmenityResponse create(@PathVariable Long hotelId, @Valid @RequestBody HotelAmenityRequest request) {
        return toResponse(service.create(hotelId, request));
    }

    @PutMapping("/{hotelId}/amenities/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public HotelAmenityResponse update(@PathVariable Long hotelId, @PathVariable Long id,
                                       @Valid @RequestBody HotelAmenityRequest request) {
        return toResponse(service.update(hotelId, id, request));
    }

    @DeleteMapping("/{hotelId}/amenities/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void delete(@PathVariable Long hotelId, @PathVariable Long id) {
        service.delete(hotelId, id);
    }

    private HotelAmenityResponse toResponse(HotelAmenity amenity) {
        return new HotelAmenityResponse(amenity.getId(), amenity.getHotel().getId(),
                amenity.getName(), amenity.getDescription(), amenity.isIncluded());
    }
}
