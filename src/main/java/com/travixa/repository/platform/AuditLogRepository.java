package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
