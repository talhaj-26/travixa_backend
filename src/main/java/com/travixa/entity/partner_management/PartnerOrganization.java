package com.travixa.entity.partner_management;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.PartnerBusinessType;
import com.travixa.enums.PartnerOrganizationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partner_organizations", indexes = {
        @Index(name = "idx_partner_org_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_partner_org_slug", columnNames = "slug"),
        @UniqueConstraint(name = "uk_partner_org_registration", columnNames = "registration_number")
})
@Getter
@Setter
@NoArgsConstructor
public class PartnerOrganization extends BaseAuditEntity {

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String name;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String slug;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PartnerBusinessType businessType;

    @Size(max = 180)
    @Column(length = 180)
    private String legalName;

    @Size(max = 100)
    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String contactEmail;

    @Size(max = 32)
    @Column(length = 32)
    private String contactPhone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PartnerOrganizationStatus status = PartnerOrganizationStatus.PENDING;
}
