package com.travixa.services.hotel;

import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.travixa.dto.hotel.HotelRequest;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.ListingStatus;
import com.travixa.mapper.HotelMapper;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final DestinationRepository destinationRepository;
    private final PartnerOrganizationRepository organizationRepository;

    public HotelService(
            HotelRepository hotelRepository,
            DestinationRepository destinationRepository,
            PartnerOrganizationRepository organizationRepository
    ) {
        this.hotelRepository = hotelRepository;
        this.destinationRepository = destinationRepository;
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Hotel create(HotelRequest request) {
        PartnerOrganization organization = organizationRepository
                .findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Organization not found"));

        Destination destination = destinationRepository
                .findById(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found"));

        if (hotelRepository.existsByOrganization_IdAndSlug(
                organization.getId(), request.slug().trim().toLowerCase()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Hotel slug already exists for this organization");
        }

        if (request.listingStatus() == ListingStatus.PUBLISHED
                && !destination.isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Publish the destination before publishing its hotel");
        }

        Hotel hotel = HotelMapper.toEntity(request, organization, destination);
        hotel.setSlug(request.slug().trim().toLowerCase());

        return hotelRepository.save(hotel);
    }

    @Transactional
    public Hotel update(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"));

        PartnerOrganization organization = organizationRepository
                .findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Organization not found"));

        Destination destination = destinationRepository
                .findById(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Destination not found"));

        String slug = request.slug().trim().toLowerCase();

        if (hotelRepository.existsByOrganization_IdAndSlugAndIdNot(
                organization.getId(), slug, id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Hotel slug already exists for this organization");
        }

        if (request.listingStatus() == ListingStatus.PUBLISHED
                && !destination.isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Publish the destination before publishing its hotel");
        }

        hotel.setOrganization(organization);
        hotel.setDestination(destination);
        HotelMapper.updateEntity(hotel, request);
        hotel.setSlug(slug);

        return hotelRepository.save(hotel);
    }

    @Transactional
    public void delete(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hotel not found"));

        try {
            hotelRepository.delete(hotel);
            hotelRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this hotel because it is linked to other records",
                    ex);
        }
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }

    @Transactional
    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Transactional
    public void deleteById(Long id) {
        hotelRepository.deleteById(id);
    }

    public List<Hotel> findPublished() {
        return hotelRepository.findAllByListingStatusOrderByNameAsc(
                com.travixa.enums.ListingStatus.PUBLISHED
        );
    }

    public Optional<Hotel> findPublishedById(Long id) {
        return hotelRepository.findByIdAndListingStatus(
                id,
                com.travixa.enums.ListingStatus.PUBLISHED
        );
    }
}
