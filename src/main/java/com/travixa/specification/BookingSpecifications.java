package com.travixa.specification;

import com.travixa.entity.booking.Booking;
import com.travixa.enums.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class BookingSpecifications {

    private BookingSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Booking> filter(String customerUsername, BookingStatus status,
            Instant placedFrom, Instant placedUntil) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (customerUsername != null && !customerUsername.isBlank()) {
                predicates.add(builder.equal(root.join("customer").get("userName"), customerUsername.trim()));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (placedFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("placedAt"), placedFrom));
            }
            if (placedUntil != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("placedAt"), placedUntil));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
