package com.travixa.entity.partner_management;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.PartnerMembershipRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partner_memberships", indexes = {
        @Index(name = "idx_partner_membership_user", columnList = "user_name"),
        @Index(name = "idx_partner_membership_org_active", columnList = "organization_id, active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_partner_membership_user_org", columnNames = { "user_name", "organization_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class PartnerMembership extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PartnerMembershipRole membershipRole;

    @Column(nullable = false)
    private boolean active = true;
}
