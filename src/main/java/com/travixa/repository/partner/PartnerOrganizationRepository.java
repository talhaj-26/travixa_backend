package com.travixa.repository.partner;

import com.travixa.entity.partner_management.PartnerOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.travixa.enums.PartnerOrganizationStatus;

import java.util.Optional;

public interface PartnerOrganizationRepository
        extends JpaRepository<PartnerOrganization, Long>, JpaSpecificationExecutor<PartnerOrganization> {

    Optional<PartnerOrganization> findBySlug(String slug);

    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);

    long countByStatus(PartnerOrganizationStatus status);
}
