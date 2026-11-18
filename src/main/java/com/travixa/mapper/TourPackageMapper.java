package com.travixa.mapper;

import com.travixa.dto.tour.TourPackageRequest;
import com.travixa.dto.tour.TourPackageResponse;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.tour_guide.TourPackage;

import java.util.Objects;

public final class TourPackageMapper {

    private TourPackageMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static TourPackage toEntity(TourPackageRequest request, PartnerOrganization organization,
            Destination destination) {
        TourPackage tourPackage = new TourPackage();
        tourPackage.setOrganization(Objects.requireNonNull(organization, "organization must not be null"));
        tourPackage.setDestination(Objects.requireNonNull(destination, "destination must not be null"));
        updateEntity(tourPackage, request);
        return tourPackage;
    }

    public static void updateEntity(TourPackage tourPackage, TourPackageRequest request) {
        tourPackage.setName(request.name());
        tourPackage.setSlug(request.slug());
        tourPackage.setDescription(request.description());
        tourPackage.setInclusions(request.inclusions());
        tourPackage.setExclusions(request.exclusions());
        tourPackage.setCancellationPolicy(request.cancellationPolicy());
        tourPackage.setBookingDeadlineHours(request.bookingDeadlineHours());
        tourPackage.setDurationDays(request.durationDays());
        tourPackage.setMaximumGroupSize(request.maximumGroupSize());
        tourPackage.setBasePrice(request.basePrice());
        tourPackage.setCurrency(request.currency());
        tourPackage.setListingStatus(request.listingStatus());
    }

    public static TourPackageResponse toResponse(TourPackage tourPackage) {
        return new TourPackageResponse(tourPackage.getId(), tourPackage.getOrganization().getId(),
                tourPackage.getDestination().getId(), tourPackage.getDestination().getName(), tourPackage.getName(), tourPackage.getSlug(),
                tourPackage.getDescription(), tourPackage.getInclusions(), tourPackage.getExclusions(),
                tourPackage.getCancellationPolicy(), tourPackage.getBookingDeadlineHours(),
                tourPackage.getDurationDays(), tourPackage.getMaximumGroupSize(),
                tourPackage.getBasePrice(), tourPackage.getCurrency(), tourPackage.getListingStatus());
    }
}
