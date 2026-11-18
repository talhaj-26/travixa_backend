package com.travixa.entity.tour_guide;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.CustomTripRequestStatus;
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
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "custom_trip_requests", indexes = {
        @Index(name = "idx_custom_trip_user_status", columnList = "user_name, status")
})
@Getter
@Setter
@NoArgsConstructor
public class CustomTripRequest extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Positive
    @Column(name = "traveler_count", nullable = false)
    private int travelerCount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "target_budget", precision = 14, scale = 2)
    private BigDecimal targetBudget;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @Size(max = 4000)
    @Column(length = 4000)
    private String requirements;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private CustomTripRequestStatus status = CustomTripRequestStatus.SUBMITTED;
}
