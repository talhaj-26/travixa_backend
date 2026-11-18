package com.travixa.repository.partner;

import com.travixa.entity.partner_management.PartnerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartnerApplicationRepository
        extends JpaRepository<PartnerApplication, Long>, JpaSpecificationExecutor<PartnerApplication> {
}
