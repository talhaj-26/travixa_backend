package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guide_service_areas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_guide_service_area", columnNames = { "guide_profile_id", "destination_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class GuideServiceArea extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_profile_id", nullable = false)
    private TourGuideProfile guideProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @NotBlank
    @Size(max = 160)
    @Column(name = "area_name", nullable = false, length = 160)
    private String areaName;
}
