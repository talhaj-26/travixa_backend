package com.travixa.specification;

import com.travixa.entity.hotel_stays.Destination;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DestinationSpecifications {

    private DestinationSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Destination> filter(String search, String countryCode, String region,
            Boolean published) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("slug")), pattern)));
            }
            if (countryCode != null && !countryCode.isBlank()) {
                predicates.add(builder.equal(builder.upper(root.get("countryCode")),
                        countryCode.trim().toUpperCase(Locale.ROOT)));
            }
            if (region != null && !region.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("region")),
                        "%" + region.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (published != null) {
                predicates.add(builder.equal(root.get("published"), published));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
