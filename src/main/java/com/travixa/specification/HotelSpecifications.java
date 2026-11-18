package com.travixa.specification;

import com.travixa.entity.hotel_stays.Hotel;
import com.travixa.enums.ListingStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HotelSpecifications {

    private HotelSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Hotel> filter(Long organizationId, Long destinationId, ListingStatus status,
            Integer minimumStars, Integer maximumStars, String search) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (organizationId != null) {
                predicates.add(builder.equal(root.join("organization").get("id"), organizationId));
            }
            if (destinationId != null) {
                predicates.add(builder.equal(root.join("destination").get("id"), destinationId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("listingStatus"), status));
            }
            if (minimumStars != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("starRating"), minimumStars));
            }
            if (maximumStars != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("starRating"), maximumStars));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("address")), pattern)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
