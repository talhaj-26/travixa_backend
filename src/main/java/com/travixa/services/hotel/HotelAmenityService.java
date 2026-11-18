package com.travixa.services.hotel;

import com.travixa.dto.hotel.HotelAmenityRequest;
import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.entity.hotel_stays.HotelAmenity;
import com.travixa.enums.ListingStatus;
import com.travixa.repository.hotel.HotelAmenityRepository;
import com.travixa.repository.hotel.HotelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HotelAmenityService {
    private final HotelAmenityRepository amenities;
    private final HotelRepository hotels;

    public HotelAmenityService(HotelAmenityRepository amenities, HotelRepository hotels) {
        this.amenities = amenities;
        this.hotels = hotels;
    }

    public List<HotelAmenity> findPublished(Long hotelId) {
        requirePublishedHotel(hotelId);
        return amenities.findAllByHotel_IdOrderByNameAscIdAsc(hotelId);
    }

    public List<HotelAmenity> findForAdmin(Long hotelId) {
        requireHotel(hotelId);
        return amenities.findAllByHotel_IdOrderByNameAscIdAsc(hotelId);
    }

    @Transactional
    public HotelAmenity create(Long hotelId, HotelAmenityRequest request) {
        if (!hotelId.equals(request.hotelId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amenity hotelId must match the URL");
        }
        Hotel hotel = requireHotel(hotelId);
        String name = normalizeName(request.name());
        if (amenities.existsByHotel_IdAndNameIgnoreCase(hotelId, name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This amenity already exists for the hotel");
        }
        HotelAmenity amenity = new HotelAmenity();
        amenity.setHotel(hotel);
        apply(amenity, request, name);
        return amenities.save(amenity);
    }

    @Transactional
    public HotelAmenity update(Long hotelId, Long id, HotelAmenityRequest request) {
        if (!hotelId.equals(request.hotelId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amenity hotelId must match the URL");
        }
        HotelAmenity amenity = amenities.findByIdAndHotel_Id(id, hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel amenity not found"));
        String name = normalizeName(request.name());
        if (amenities.existsByHotel_IdAndNameIgnoreCaseAndIdNot(hotelId, name, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This amenity already exists for the hotel");
        }
        apply(amenity, request, name);
        return amenities.save(amenity);
    }

    @Transactional
    public void delete(Long hotelId, Long id) {
        HotelAmenity amenity = amenities.findByIdAndHotel_Id(id, hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel amenity not found"));
        amenities.delete(amenity);
    }

    private Hotel requireHotel(Long hotelId) {
        return hotels.findById(hotelId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
    }

    private Hotel requirePublishedHotel(Long hotelId) {
        return hotels.findByIdAndListingStatus(hotelId, ListingStatus.PUBLISHED).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Published hotel not found"));
    }

    private String normalizeName(String name) {
        String normalized = name.trim();
        if (normalized.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amenity name is required");
        return normalized;
    }

    private void apply(HotelAmenity amenity, HotelAmenityRequest request, String name) {
        amenity.setName(name);
        amenity.setDescription(request.description() == null || request.description().isBlank()
                ? null : request.description().trim());
        amenity.setIncluded(request.included());
    }
}
