package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.TourGuideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tour_guide_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tour_guide_user", columnNames = "user_name")
})
@Getter
@Setter
@NoArgsConstructor
public class TourGuideProfile extends BaseAuditEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private PartnerOrganization organization;

    @Size(max = 3000)
    @Column(length = 3000)
    private String biography;

    @Size(max = 80)
    @Column(name = "license_number", length = 80)
    private String licenseNumber;

    @Size(max = 500)
    @Column(name = "language_codes", length = 500)
    private String languageCodes;

    @NotNull
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TourGuideStatus status = TourGuideStatus.PENDING_VERIFICATION;
}
