package com.travixa.controllers;

import com.travixa.dto.tour.TourPackageResponse;
import com.travixa.dto.tour.TourPackageRequest;
import com.travixa.dto.tour.PackageDepartureResponse;
import com.travixa.dto.tour.PackageImageResponse;
import com.travixa.dto.tour.PackageItineraryResponse;
import com.travixa.entity.tour_guide.PackageDeparture;
import com.travixa.entity.tour_guide.PackageImage;
import com.travixa.entity.tour_guide.PackageItineraryDay;
import com.travixa.entity.tour_guide.TourPackage;
import com.travixa.mapper.TourPackageMapper;
import com.travixa.services.tour.TourPackageService;
import com.travixa.services.tour.TourPackageCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/tours-packages")
//@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TourPackageController {

    private final TourPackageService tourPackageService;
    private final TourPackageCatalogService catalogService;

    public TourPackageController(TourPackageService tourPackageService, TourPackageCatalogService catalogService) {
        this.tourPackageService = tourPackageService;
        this.catalogService = catalogService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TourPackageResponse> findPublished() {
        return tourPackageService.findPublished().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public TourPackageResponse findPublishedById(@PathVariable Long id) {
        return tourPackageService.findPublishedById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published tour package not found"));
    }

    @GetMapping("/{id}/itineraries")
    public List<PackageItineraryResponse> itineraries(@PathVariable Long id) {
        return catalogService.itineraries(id, true).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}/departures")
    public List<PackageDepartureResponse> departures(@PathVariable Long id) {
        return catalogService.departures(id, true).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}/images")
    public List<PackageImageResponse> images(@PathVariable Long id) {
        return catalogService.images(id, true).stream().map(this::toResponse).toList();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional(readOnly = true)
    public List<TourPackageResponse> findAllForAdmin() { return tourPackageService.findAll().stream().map(this::toResponse).toList(); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public TourPackageResponse create(@Valid @RequestBody TourPackageRequest request) { return toResponse(tourPackageService.create(request)); }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    @Transactional
    public TourPackageResponse update(@PathVariable Long id, @Valid @RequestBody TourPackageRequest request) { return toResponse(tourPackageService.update(id, request)); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public void delete(@PathVariable Long id) { tourPackageService.delete(id); }

    private TourPackageResponse toResponse(TourPackage tourPackage) {
        return TourPackageMapper.toResponse(tourPackage);
    }
    private PackageItineraryResponse toResponse(PackageItineraryDay item) {
        return new PackageItineraryResponse(item.getId(), item.getTourPackage().getId(), item.getDayNumber(), item.getTitle(), item.getDescription(), item.getLocationName());
    }
    private PackageDepartureResponse toResponse(PackageDeparture item) {
        return new PackageDepartureResponse(item.getId(), item.getTourPackage().getId(), item.getStartsAt(), item.getEndsAt(), item.getSeatCapacity(), item.getSeatsRemaining(), item.getPriceOverride(), item.getCurrency(), item.getStatus());
    }
    private PackageImageResponse toResponse(PackageImage item) {
        return new PackageImageResponse(item.getId(), item.getTourPackage().getId(), item.getStorageKey(), "/api/media/" + item.getStorageKey(), item.getAltText(), item.getDisplayOrder(), item.getDisplayOrder() == 0);
    }
}
