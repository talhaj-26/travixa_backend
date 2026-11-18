package com.travixa.specification;

import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerOrganizationStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PartnerOrganizationSpecifications {

    private PartnerOrganizationSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<PartnerOrganization> filter(PartnerOrganizationStatus status,
            PartnerBusinessType businessType, String search) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (businessType != null) {
                predicates.add(builder.equal(root.get("businessType"), businessType));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("legalName")), pattern),
                        builder.like(builder.lower(root.get("registrationNumber")), pattern),
                        builder.like(builder.lower(root.get("contactEmail")), pattern)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
