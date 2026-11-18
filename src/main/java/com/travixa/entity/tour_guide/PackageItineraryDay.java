package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "package_itinerary_days", uniqueConstraints = {
        @UniqueConstraint(name = "uk_itinerary_package_day", columnNames = { "package_id", "day_number" })
})
@Getter
@Setter
@NoArgsConstructor
public class PackageItineraryDay extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;

    @Positive
    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String title;

    @Size(max = 4000)
    @Column(length = 4000)
    private String description;

    @Size(max = 160)
    @Column(length = 160)
    private String locationName;
}
