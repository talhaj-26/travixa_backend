package com.travixa.specification;

import com.travixa.entity.transport.TransportSchedule;
import com.travixa.enums.ScheduleStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class TransportScheduleSpecifications {

    private TransportScheduleSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<TransportSchedule> filter(Long organizationId, Long routeId,
            ScheduleStatus status, Instant departureFrom, Instant departureUntil) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (organizationId != null) {
                predicates.add(builder.equal(root.join("organization").get("id"), organizationId));
            }
            if (routeId != null) {
                predicates.add(builder.equal(root.join("route").get("id"), routeId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (departureFrom != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("departureAt"), departureFrom));
            }
            if (departureUntil != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("departureAt"), departureUntil));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
