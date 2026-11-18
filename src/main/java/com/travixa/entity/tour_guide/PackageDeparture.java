package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "package_departures", indexes = {
        @Index(name = "idx_package_departures_package_start", columnList = "package_id, starts_at")
})
@Getter
@Setter
@NoArgsConstructor
public class PackageDeparture extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private TourPackage tourPackage;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Positive
    @Column(name = "seat_capacity", nullable = false)
    private int seatCapacity;

    @PositiveOrZero
    @Column(name = "seats_remaining", nullable = false)
    private int seatsRemaining;

    @Digits(integer = 12, fraction = 2)
    @Column(name = "price_override", precision = 14, scale = 2)
    private BigDecimal priceOverride;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;
}
