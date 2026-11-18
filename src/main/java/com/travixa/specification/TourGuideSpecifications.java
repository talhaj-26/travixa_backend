package com.travixa.specification;

import com.travixa.entity.tour_guide.TourGuideProfile;
import com.travixa.enums.TourGuideStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TourGuideSpecifications {

    private TourGuideSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<TourGuideProfile> filter(Long organizationId, String username,
            TourGuideStatus status, String languageCode) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (organizationId != null) {
                predicates.add(builder.equal(root.join("organization").get("id"), organizationId));
            }
            if (username != null && !username.isBlank()) {
                predicates.add(builder.equal(root.join("user").get("userName"), username.trim()));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (languageCode != null && !languageCode.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("languageCodes")),
                        "%" + languageCode.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
