package com.travixa.entity.restaurant;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "restaurant_buffet_schedules", indexes = {
        @Index(name = "idx_buffet_restaurant_date", columnList = "restaurant_id, buffet_date")
})
@Getter
@Setter
@NoArgsConstructor
public class BuffetSchedule extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotNull
    @Column(name = "buffet_date", nullable = false)
    private LocalDate buffetDate;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private LocalTime startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private LocalTime endsAt;

    @NotNull @Positive
    @Column(name = "adult_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal adultPrice;

    @PositiveOrZero
    @Column(name = "child_price", precision = 14, scale = 2)
    private BigDecimal childPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @Positive
    @Column(nullable = false)
    private int capacity;

    @Size(max = 2000)
    @Column(name = "menu_summary", length = 2000)
    private String menuSummary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false, length = 24)
    private ListingStatus listingStatus = ListingStatus.DRAFT;
}
