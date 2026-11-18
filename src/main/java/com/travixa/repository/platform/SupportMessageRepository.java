package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
}
