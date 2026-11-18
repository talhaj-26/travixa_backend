package com.travixa.repository.partner;

import com.travixa.entity.partner_management.PartnerMembership;
import com.travixa.enums.PartnerOrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PartnerMembershipRepository extends JpaRepository<PartnerMembership, Long> {

    @Query("""
            select case when count(m) > 0 then true else false end
            from PartnerMembership m
            where m.user.userName = :username
              and m.organization.id = :organizationId
              and m.active = true
              and m.organization.status = :organizationStatus
            """)
    boolean existsActiveMembership(
            @Param("username") String username,
            @Param("organizationId") Long organizationId,
            @Param("organizationStatus") PartnerOrganizationStatus organizationStatus);

    List<PartnerMembership> findByUser_UserNameAndActiveTrue(
            String userName
    );
}
