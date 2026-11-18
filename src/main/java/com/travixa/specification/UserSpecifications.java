package com.travixa.specification;

import com.travixa.entity.identity_access.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class UserSpecifications {

    private UserSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<User> filter(String search, Boolean enabled, String roleName) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(builder.like(builder.lower(root.get("userName")), pattern),
                        builder.like(builder.lower(root.get("userFirstName")), pattern),
                        builder.like(builder.lower(root.get("userLastName")), pattern),
                        builder.like(builder.lower(root.get("email")), pattern)));
            }
            if (enabled != null) {
                predicates.add(builder.equal(root.get("enabled"), enabled));
            }
            if (roleName != null && !roleName.isBlank()) {
                predicates.add(builder.equal(builder.upper(root.join("roles").get("name")),
                        roleName.trim().toUpperCase(Locale.ROOT)));
                query.distinct(true);
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
