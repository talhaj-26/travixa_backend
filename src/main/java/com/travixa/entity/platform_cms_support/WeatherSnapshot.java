package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "weather_snapshots", indexes = {
        @Index(name = "idx_weather_snapshot_destination_observed", columnList = "destination_id, observed_at")
})
@Getter
@Setter
@NoArgsConstructor
public class WeatherSnapshot extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Size(max = 160)
    @Column(name = "location_name", length = 160)
    private String locationName;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperatureCelsius;

    @Size(max = 80)
    @Column(name = "condition_code", length = 80)
    private String conditionCode;

    @NotNull
    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
