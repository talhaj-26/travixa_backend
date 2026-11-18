package com.travixa.specification;

import com.travixa.entity.payment_financial.Coupon;
import com.travixa.enums.CouponStatus;
import com.travixa.enums.CouponType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CouponSpecifications {

    private CouponSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Coupon> filter(String code, CouponStatus status, CouponType type, Instant activeAt) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (code != null && !code.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("code")),
                        "%" + code.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            if (activeAt != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("startsAt"), activeAt));
                predicates.add(builder.greaterThanOrEqualTo(root.get("expiresAt"), activeAt));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
