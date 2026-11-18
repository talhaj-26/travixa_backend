package com.travixa.services.tour;

import com.travixa.dto.tour.TourPackageRequest;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.tour_guide.TourPackage;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerOrganizationStatus;
import com.travixa.mapper.TourPackageMapper;
import com.travixa.repository.hotel.DestinationRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.tour.TourPackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final DestinationRepository destinationRepository;
    private final PartnerOrganizationRepository organizationRepository;

    public TourPackageService(TourPackageRepository tourPackageRepository,
                              DestinationRepository destinationRepository,
                              PartnerOrganizationRepository organizationRepository) {
        this.tourPackageRepository = tourPackageRepository;
        this.destinationRepository = destinationRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<TourPackage> findAll() {
        return tourPackageRepository.findAll();
    }

    public Optional<TourPackage> findById(Long id) {
        return tourPackageRepository.findById(id);
    }

    public List<TourPackage> findPublished() {
        return tourPackageRepository.findAllByListingStatusAndOrganization_StatusAndDestination_PublishedOrderByNameAsc(
                ListingStatus.PUBLISHED, PartnerOrganizationStatus.ACTIVE, true);
    }

    public Optional<TourPackage> findPublishedById(Long id) {
        return tourPackageRepository.findByIdAndListingStatusAndOrganization_StatusAndDestination_Published(
                id, ListingStatus.PUBLISHED, PartnerOrganizationStatus.ACTIVE, true);
    }

    @Transactional
    public TourPackage create(TourPackageRequest request) {
        PartnerOrganization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
        validateAgency(organization);
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found"));
        String slug = request.slug().trim().toLowerCase();
        if (tourPackageRepository.existsByOrganization_IdAndSlug(organization.getId(), slug)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tour package slug already exists for this organization");
        }
        validatePublication(request.listingStatus(), destination, organization);
        TourPackage tourPackage = TourPackageMapper.toEntity(request, organization, destination);
        tourPackage.setSlug(slug);
        return tourPackageRepository.save(tourPackage);
    }

    @Transactional
    public TourPackage update(Long id, TourPackageRequest request) {
        TourPackage tourPackage = tourPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour package not found"));
        PartnerOrganization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
        validateAgency(organization);
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found"));
        String slug = request.slug().trim().toLowerCase();
        if (tourPackageRepository.existsByOrganization_IdAndSlugAndIdNot(organization.getId(), slug, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tour package slug already exists for this organization");
        }
        validatePublication(request.listingStatus(), destination, organization);
        tourPackage.setOrganization(organization);
        tourPackage.setDestination(destination);
        TourPackageMapper.updateEntity(tourPackage, request);
        tourPackage.setSlug(slug);
        return tourPackageRepository.save(tourPackage);
    }

    @Transactional
    public void delete(Long id) {
        TourPackage tourPackage = tourPackageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour package not found"));
        try {
            tourPackageRepository.delete(tourPackage);
            tourPackageRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete this tour package because it is linked to other records", ex);
        }
    }

    @Transactional
    public TourPackage save(TourPackage tourPackage) {
        return tourPackageRepository.save(tourPackage);
    }

    private void validateAgency(PartnerOrganization organization) {
        if (organization.getBusinessType() != PartnerBusinessType.TRAVEL_AGENCY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tour packages must belong to a travel agency organization");
        }
    }

    private void validatePublication(ListingStatus listingStatus, Destination destination, PartnerOrganization organization) {
        if (listingStatus == ListingStatus.PUBLISHED && !destination.isPublished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Publish the destination before publishing its tour package");
        }
        if (listingStatus == ListingStatus.PUBLISHED && organization.getStatus() != PartnerOrganizationStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Activate the travel agency before publishing its tour package");
        }
    }
}
