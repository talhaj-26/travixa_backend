package com.travixa.specification;

import com.travixa.entity.tour_guide.TourPackage;
import com.travixa.enums.ListingStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TourPackageSpecifications {

    private TourPackageSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<TourPackage> filter(Long organizationId, Long destinationId,
            ListingStatus status, BigDecimal minimumPrice, BigDecimal maximumPrice, Integer minimumDuration,
            Integer maximumDuration, String search) {
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
            if (minimumPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("basePrice"), minimumPrice));
            }
            if (maximumPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("basePrice"), maximumPrice));
            }
            if (minimumDuration != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("durationDays"), minimumDuration));
            }
            if (maximumDuration != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("durationDays"), maximumDuration));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("slug")), pattern)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
