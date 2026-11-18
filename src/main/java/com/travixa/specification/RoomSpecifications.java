package com.travixa.specification;

import com.travixa.entity.hotel_stays.Room;
import com.travixa.enums.RoomStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RoomSpecifications {

    private RoomSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Room> filter(Long hotelId, Long roomTypeId, RoomStatus status, String roomNumber) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (hotelId != null) {
                predicates.add(builder.equal(root.join("hotel").get("id"), hotelId));
            }
            if (roomTypeId != null) {
                predicates.add(builder.equal(root.join("roomType").get("id"), roomTypeId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (roomNumber != null && !roomNumber.isBlank()) {
                predicates.add(builder.like(builder.lower(root.get("roomNumber")),
                        "%" + roomNumber.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
