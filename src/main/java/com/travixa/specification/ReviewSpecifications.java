package com.travixa.specification;

import com.travixa.entity.customer_engagement.Review;
import com.travixa.enums.ReviewStatus;
import com.travixa.enums.ReviewTargetType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class ReviewSpecifications {

    private ReviewSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Review> filter(String username, ReviewTargetType targetType, Long targetId,
            ReviewStatus status, Integer minimumRating, Integer maximumRating) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null && !username.isBlank()) {
                predicates.add(builder.equal(root.join("user").get("userName"), username.trim()));
            }
            if (targetType != null) {
                predicates.add(builder.equal(root.get("targetType"), targetType));
            }
            if (targetId != null) {
                predicates.add(builder.equal(root.get("targetId"), targetId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (minimumRating != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("rating"), minimumRating));
            }
            if (maximumRating != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("rating"), maximumRating));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
