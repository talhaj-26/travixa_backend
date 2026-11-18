package com.travixa.entity.transport;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.VehicleStatus;
import com.travixa.enums.VehicleType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles", indexes = {
        @Index(name = "idx_vehicles_org_status", columnList = "organization_id, status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehicle_org_registration", columnNames = { "organization_id",
                "registration_number" })
})
@Getter
@Setter
@NoArgsConstructor
public class Vehicle extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 24)
    private VehicleType vehicleType;

    @NotBlank
    @Size(max = 32)
    @Column(name = "registration_number", nullable = false, length = 32)
    private String registrationNumber;

    @Size(max = 80)
    @Column(length = 80)
    private String make;

    @Size(max = 80)
    @Column(name = "model_name", length = 80)
    private String modelName;

    @Positive
    @Column(name = "seat_capacity", nullable = false)
    private int seatCapacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private VehicleStatus status = VehicleStatus.ACTIVE;
}
