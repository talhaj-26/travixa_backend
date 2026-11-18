package com.travixa.entity.transport;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.RouteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_routes", indexes = {
        @Index(name = "idx_transport_routes_org_status", columnList = "organization_id, status")
})
@Getter
@Setter
@NoArgsConstructor
public class TransportRoute extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_destination_id")
    private Destination originDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @NotBlank
    @Size(max = 160)
    @Column(name = "origin_name", nullable = false, length = 160)
    private String originName;

    @NotBlank
    @Size(max = 160)
    @Column(name = "destination_name", nullable = false, length = 160)
    private String destinationName;

    @PositiveOrZero
    @Column(name = "distance_km", precision = 9, scale = 2)
    private BigDecimal distanceKm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RouteStatus status = RouteStatus.DRAFT;
}
