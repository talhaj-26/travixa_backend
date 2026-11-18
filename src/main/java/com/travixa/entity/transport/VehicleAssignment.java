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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "vehicle_assignments", indexes = {
        @Index(name = "idx_vehicle_assignment_vehicle_start", columnList = "vehicle_id, starts_at"),
        @Index(name = "idx_vehicle_assignment_driver_start", columnList = "driver_profile_id, starts_at")
})
@Getter
@Setter
@NoArgsConstructor
public class VehicleAssignment extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_profile_id", nullable = false)
    private DriverProfile driverProfile;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    private Instant endsAt;
}
