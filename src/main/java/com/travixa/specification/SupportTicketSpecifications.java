package com.travixa.specification;

import com.travixa.entity.platform_cms_support.SupportTicket;
import com.travixa.enums.SupportTicketStatus;
import com.travixa.enums.TicketPriority;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SupportTicketSpecifications {

    private SupportTicketSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<SupportTicket> filter(Long organizationId, String customerUsername,
            String assignedUsername, SupportTicketStatus status, TicketPriority priority, String search) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (organizationId != null) {
                predicates.add(builder.equal(root.join("organization").get("id"), organizationId));
            }
            if (customerUsername != null && !customerUsername.isBlank()) {
                predicates.add(builder.equal(root.join("customer").get("userName"), customerUsername.trim()));
            }
            if (assignedUsername != null && !assignedUsername.isBlank()) {
                predicates.add(builder.equal(root.join("assignedTo").get("userName"), assignedUsername.trim()));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(builder.equal(root.get("priority"), priority));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("referenceNumber")), pattern),
                        builder.like(builder.lower(root.get("subject")), pattern),
                        builder.like(builder.lower(root.get("category")), pattern)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
