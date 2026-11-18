package com.travixa.specification;

import com.travixa.entity.payment_financial.Payment;
import com.travixa.enums.PaymentMethod;
import com.travixa.enums.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class PaymentSpecifications {

    private PaymentSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Payment> filter(Long bookingId, PaymentStatus status, PaymentMethod method,
            BigDecimal minimumAmount, BigDecimal maximumAmount) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (bookingId != null) {
                predicates.add(builder.equal(root.join("booking").get("id"), bookingId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (method != null) {
                predicates.add(builder.equal(root.get("method"), method));
            }
            if (minimumAmount != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("amount"), minimumAmount));
            }
            if (maximumAmount != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("amount"), maximumAmount));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
