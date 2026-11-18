package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RatePlanType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_rates", indexes = {
        @Index(name = "idx_room_rates_type_dates", columnList = "room_type_id, valid_from, valid_until")
})
@Getter
@Setter
@NoArgsConstructor
public class RoomRate extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rate_plan_type", nullable = false, length = 24)
    private RatePlanType ratePlanType;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    @Column(name = "nightly_rate", nullable = false, precision = 14, scale = 2)
    private BigDecimal nightlyRate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @NotNull
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(nullable = false)
    private boolean active = true;
}
