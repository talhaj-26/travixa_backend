package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.GuideAvailabilityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "guide_availability", indexes = {
        @Index(name = "idx_guide_availability_guide_start", columnList = "guide_profile_id, starts_at")
})
@Getter
@Setter
@NoArgsConstructor
public class GuideAvailability extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_profile_id", nullable = false)
    private TourGuideProfile guideProfile;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GuideAvailabilityStatus status = GuideAvailabilityStatus.AVAILABLE;
}
