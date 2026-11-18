package com.travixa.entity.transport;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.partner_management.PartnerOrganization;
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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transport_schedules", indexes = {
        @Index(name = "idx_transport_schedule_org_departure", columnList = "organization_id, departure_at"),
        @Index(name = "idx_transport_schedule_route_departure", columnList = "route_id, departure_at")
})
@Getter
@Setter
@NoArgsConstructor
public class TransportSchedule extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private TransportRoute route;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @Column(name = "departure_at", nullable = false)
    private Instant departureAt;

    @NotNull
    @Column(name = "arrival_at", nullable = false)
    private Instant arrivalAt;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "ticket_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal ticketPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;
}
