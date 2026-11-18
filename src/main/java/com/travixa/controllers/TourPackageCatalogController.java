package com.travixa.controllers;

import com.travixa.dto.tour.*;
import com.travixa.entity.tour_guide.PackageDeparture;
import com.travixa.entity.tour_guide.PackageImage;
import com.travixa.entity.tour_guide.PackageItineraryDay;
import com.travixa.services.tour.TourPackageCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tour-package-catalog")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class TourPackageCatalogController {
    private final TourPackageCatalogService service;

    public TourPackageCatalogController(TourPackageCatalogService service) { this.service = service; }

    @GetMapping("/{packageId}/itineraries") public List<PackageItineraryResponse> itineraries(@PathVariable Long packageId) { return service.itineraries(packageId, false).stream().map(this::itineraryResponse).toList(); }
    @PostMapping("/itineraries") @ResponseStatus(HttpStatus.CREATED) public PackageItineraryResponse createItinerary(@Valid @RequestBody PackageItineraryRequest request) { return itineraryResponse(service.createItinerary(request)); }
    @PutMapping("/itineraries/{id}") public PackageItineraryResponse updateItinerary(@PathVariable Long id, @Valid @RequestBody PackageItineraryRequest request) { return itineraryResponse(service.updateItinerary(id, request)); }
    @DeleteMapping("/itineraries/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteItinerary(@PathVariable Long id) { service.deleteItinerary(id); }

    @GetMapping("/{packageId}/departures") public List<PackageDepartureResponse> departures(@PathVariable Long packageId) { return service.departures(packageId, false).stream().map(this::departureResponse).toList(); }
    @PostMapping("/departures") @ResponseStatus(HttpStatus.CREATED) public PackageDepartureResponse createDeparture(@Valid @RequestBody PackageDepartureRequest request) { return departureResponse(service.createDeparture(request)); }
    @PutMapping("/departures/{id}") public PackageDepartureResponse updateDeparture(@PathVariable Long id, @Valid @RequestBody PackageDepartureRequest request) { return departureResponse(service.updateDeparture(id, request)); }
    @DeleteMapping("/departures/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteDeparture(@PathVariable Long id) { service.deleteDeparture(id); }

    @GetMapping("/{packageId}/images") public List<PackageImageResponse> images(@PathVariable Long packageId) { return service.images(packageId, false).stream().map(this::imageResponse).toList(); }
    @PostMapping("/images") @ResponseStatus(HttpStatus.CREATED) public PackageImageResponse createImage(@Valid @RequestBody PackageImageRequest request) { return imageResponse(service.createImage(request)); }
    @PutMapping("/images/{id}") public PackageImageResponse updateImage(@PathVariable Long id, @Valid @RequestBody PackageImageRequest request) { return imageResponse(service.updateImage(id, request)); }
    @DeleteMapping("/images/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteImage(@PathVariable Long id) { service.deleteImage(id); }

    private PackageItineraryResponse itineraryResponse(PackageItineraryDay item) { return new PackageItineraryResponse(item.getId(), item.getTourPackage().getId(), item.getDayNumber(), item.getTitle(), item.getDescription(), item.getLocationName()); }
    private PackageDepartureResponse departureResponse(PackageDeparture item) { return new PackageDepartureResponse(item.getId(), item.getTourPackage().getId(), item.getStartsAt(), item.getEndsAt(), item.getSeatCapacity(), item.getSeatsRemaining(), item.getPriceOverride(), item.getCurrency(), item.getStatus()); }
    private PackageImageResponse imageResponse(PackageImage item) { return new PackageImageResponse(item.getId(), item.getTourPackage().getId(), item.getStorageKey(), "/api/media/" + item.getStorageKey(), item.getAltText(), item.getDisplayOrder(), item.getDisplayOrder() == 0); }
}
