package com.travixa.services.tour;

import com.travixa.dto.tour.*;
import com.travixa.entity.tour_guide.*;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.PartnerOrganizationStatus;
import com.travixa.repository.tour.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TourPackageCatalogService {
    private final TourPackageRepository packageRepository;
    private final PackageItineraryDayRepository itineraryRepository;
    private final PackageDepartureRepository departureRepository;
    private final PackageImageRepository imageRepository;

    public TourPackageCatalogService(TourPackageRepository packageRepository,
                                     PackageItineraryDayRepository itineraryRepository,
                                     PackageDepartureRepository departureRepository,
                                     PackageImageRepository imageRepository) {
        this.packageRepository = packageRepository;
        this.itineraryRepository = itineraryRepository;
        this.departureRepository = departureRepository;
        this.imageRepository = imageRepository;
    }

    public List<PackageItineraryDay> itineraries(Long packageId, boolean publishedOnly) {
        requirePackage(packageId, publishedOnly);
        return itineraryRepository.findAllByTourPackage_IdOrderByDayNumberAsc(packageId);
    }
    public List<PackageDeparture> departures(Long packageId, boolean publishedOnly) {
        requirePackage(packageId, publishedOnly);
        return departureRepository.findAllByTourPackage_IdOrderByStartsAtAsc(packageId);
    }
    public List<PackageImage> images(Long packageId, boolean publishedOnly) {
        requirePackage(packageId, publishedOnly);
        return imageRepository.findAllByTourPackage_IdOrderByDisplayOrderAscIdAsc(packageId);
    }

    @Transactional public PackageItineraryDay createItinerary(PackageItineraryRequest request) {
        TourPackage tourPackage = requirePackage(request.tourPackageId(), false);
        if (itineraryRepository.existsByTourPackage_IdAndDayNumber(tourPackage.getId(), request.dayNumber())) throw conflict("An itinerary day with this number already exists");
        PackageItineraryDay entity = new PackageItineraryDay(); entity.setTourPackage(tourPackage); applyItinerary(entity, request); return itineraryRepository.save(entity);
    }
    @Transactional public PackageItineraryDay updateItinerary(Long id, PackageItineraryRequest request) {
        PackageItineraryDay entity = itineraryRepository.findById(id).orElseThrow(() -> notFound("Itinerary day"));
        TourPackage tourPackage = requirePackage(request.tourPackageId(), false);
        if (itineraryRepository.existsByTourPackage_IdAndDayNumberAndIdNot(tourPackage.getId(), request.dayNumber(), id)) throw conflict("An itinerary day with this number already exists");
        entity.setTourPackage(tourPackage); applyItinerary(entity, request); return itineraryRepository.save(entity);
    }
    @Transactional public PackageDeparture createDeparture(PackageDepartureRequest request) { PackageDeparture entity = new PackageDeparture(); entity.setTourPackage(requirePackage(request.tourPackageId(), false)); applyDeparture(entity, request, true); return departureRepository.save(entity); }
    @Transactional public PackageDeparture updateDeparture(Long id, PackageDepartureRequest request) { PackageDeparture entity = departureRepository.findById(id).orElseThrow(() -> notFound("Package departure")); entity.setTourPackage(requirePackage(request.tourPackageId(), false)); applyDeparture(entity, request, false); return departureRepository.save(entity); }
    @Transactional public PackageImage createImage(PackageImageRequest request) { PackageImage entity = new PackageImage(); entity.setTourPackage(requirePackage(request.tourPackageId(), false)); applyImage(entity, request); return imageRepository.save(entity); }
    @Transactional public PackageImage updateImage(Long id, PackageImageRequest request) { PackageImage entity = imageRepository.findById(id).orElseThrow(() -> notFound("Package image")); entity.setTourPackage(requirePackage(request.tourPackageId(), false)); applyImage(entity, request); return imageRepository.save(entity); }
    @Transactional public void deleteItinerary(Long id) { delete(itineraryRepository, id, "Itinerary day"); }
    @Transactional public void deleteDeparture(Long id) { delete(departureRepository, id, "Package departure"); }
    @Transactional public void deleteImage(Long id) { delete(imageRepository, id, "Package image"); }

    private TourPackage requirePackage(Long id, boolean publishedOnly) {
        TourPackage item = packageRepository.findById(id).orElseThrow(() -> notFound("Tour package"));
        if (publishedOnly && (item.getListingStatus() != ListingStatus.PUBLISHED
                || item.getOrganization().getStatus() != PartnerOrganizationStatus.ACTIVE
                || !item.getDestination().isPublished())) throw notFound("Published tour package");
        return item;
    }
    private void applyItinerary(PackageItineraryDay e, PackageItineraryRequest r) { e.setDayNumber(r.dayNumber()); e.setTitle(r.title().trim()); e.setDescription(r.description() == null ? null : r.description().trim()); e.setLocationName(r.locationName() == null ? null : r.locationName().trim()); }
    private void applyDeparture(PackageDeparture e, PackageDepartureRequest r, boolean isNew) {
        if (!r.endsAt().isAfter(r.startsAt())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure end time must be after start time");
        e.setStartsAt(r.startsAt()); e.setEndsAt(r.endsAt()); e.setSeatCapacity(r.seatCapacity()); e.setPriceOverride(r.priceOverride()); e.setCurrency(r.currency()); e.setStatus(r.status());
        if (r.status() == com.travixa.enums.ScheduleStatus.FULL) e.setSeatsRemaining(0);
        else if (isNew) e.setSeatsRemaining(r.seatCapacity());
        else if (e.getSeatsRemaining() > r.seatCapacity()) e.setSeatsRemaining(r.seatCapacity());
    }
    private void applyImage(PackageImage e, PackageImageRequest r) { e.setStorageKey(r.storageKey().trim()); e.setAltText(r.altText() == null ? null : r.altText().trim()); e.setDisplayOrder(r.displayOrder()); }
    private <T> void delete(JpaRepository<T, Long> repo, Long id, String label) { if (!repo.existsById(id)) throw notFound(label); try { repo.deleteById(id); repo.flush(); } catch (org.springframework.dao.DataIntegrityViolationException ex) { throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete " + label.toLowerCase() + " because it is linked to other records", ex); } }
    private ResponseStatusException notFound(String label) { return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " not found"); }
    private ResponseStatusException conflict(String detail) { return new ResponseStatusException(HttpStatus.CONFLICT, detail); }
}
