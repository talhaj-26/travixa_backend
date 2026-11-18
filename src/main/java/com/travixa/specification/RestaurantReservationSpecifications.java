package com.travixa.specification;

import com.travixa.entity.restaurant.RestaurantReservation;
import com.travixa.enums.RestaurantReservationStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class RestaurantReservationSpecifications {

    private RestaurantReservationSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<RestaurantReservation> filter(Long restaurantId, String customerUsername,
            RestaurantReservationStatus status, Integer minimumGuests, Integer maximumGuests,
            Instant startsFrom, Instant startsUntil) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (restaurantId != null) {
                predicates.add(builder.equal(root.join("restaurant").get("id"), restaurantId));
            }
            if (customerUsername != null && !customerUsername.isBlank()) {
                predicates.add(builder.equal(root.join("customer").get("userName"), customerUsername.trim()));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (minimumGuests != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("guestCount"), minimumGuests));
            }
            if (maximumGuests != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("guestCount"), maximumGuests));
            }
            if (startsFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("startsAt"), startsFrom));
            }
            if (startsUntil != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("startsAt"), startsUntil));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
