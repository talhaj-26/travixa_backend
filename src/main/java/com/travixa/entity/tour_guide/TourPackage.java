package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tour_packages", indexes = {
        @Index(name = "idx_tour_packages_destination_status", columnList = "destination_id, listing_status"),
        @Index(name = "idx_tour_packages_organization", columnList = "organization_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_tour_package_org_slug", columnNames = { "organization_id", "slug" })
})
@Getter
@Setter
@NoArgsConstructor
public class TourPackage extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String name;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String slug;

    @Size(max = 8000)
    @Column(length = 8000)
    private String description;

    @Size(max = 4000)
    @Column(length = 4000)
    private String inclusions;

    @Size(max = 4000)
    @Column(length = 4000)
    private String exclusions;

    @Size(max = 4000)
    @Column(name = "cancellation_policy", length = 4000)
    private String cancellationPolicy;

    @PositiveOrZero
    @Column(name = "booking_deadline_hours")
    private Integer bookingDeadlineHours;

    @Positive
    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @PositiveOrZero
    @Column(name = "maximum_group_size", nullable = false)
    private int maximumGroupSize;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "base_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal basePrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false, length = 24)
    private ListingStatus listingStatus = ListingStatus.DRAFT;
}
