package com.travixa.mapper;

import com.travixa.dto.restaurant.RestaurantRequest;
import com.travixa.dto.restaurant.RestaurantResponse;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.restaurant.Restaurant;

import java.util.Objects;

public final class RestaurantMapper {

    private RestaurantMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Restaurant toEntity(RestaurantRequest request, PartnerOrganization organization,
            Destination destination) {
        Restaurant restaurant = new Restaurant();
        restaurant.setOrganization(Objects.requireNonNull(organization, "organization must not be null"));
        restaurant.setDestination(destination);
        updateEntity(restaurant, request);
        return restaurant;
    }

    public static void updateEntity(Restaurant restaurant, RestaurantRequest request) {
        restaurant.setName(request.name());
        restaurant.setSlug(request.slug());
        restaurant.setDescription(request.description());
        restaurant.setAddress(request.address());
        restaurant.setContactEmail(request.contactEmail());
        restaurant.setContactPhone(request.contactPhone());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setOpeningHours(request.openingHours());
        restaurant.setCancellationPolicy(request.cancellationPolicy());
        restaurant.setListingStatus(request.listingStatus());
    }

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(restaurant.getId(), restaurant.getOrganization().getId(),
                restaurant.getDestination() == null ? null : restaurant.getDestination().getId(),
                restaurant.getName(), restaurant.getSlug(), restaurant.getDescription(), restaurant.getAddress(),
                restaurant.getContactEmail(), restaurant.getContactPhone(), restaurant.getCuisineType(),
                restaurant.getOpeningHours(),
                restaurant.getCancellationPolicy(),
                restaurant.getListingStatus());
    }
}
