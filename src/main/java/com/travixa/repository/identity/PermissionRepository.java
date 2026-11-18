package com.travixa.repository.identity;

import com.travixa.entity.identity_access.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByName(String name);
}
