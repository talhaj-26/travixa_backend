package com.travixa.entity.transport;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "live_locations", indexes = {
        @Index(name = "idx_live_location_vehicle_recorded", columnList = "vehicle_id, recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
public class LiveLocation extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @NotNull
    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @PositiveOrZero
    @Column(name = "speed_kph", precision = 7, scale = 2)
    private BigDecimal speedKph;

    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
}
