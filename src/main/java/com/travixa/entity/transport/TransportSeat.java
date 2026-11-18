package com.travixa.entity.transport;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.TransportSeatStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transport_seats", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transport_seat_schedule_number", columnNames = { "schedule_id", "seat_number" })
})
@Getter
@Setter
@NoArgsConstructor
public class TransportSeat extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private TransportSchedule schedule;

    @NotBlank
    @Size(max = 16)
    @Column(name = "seat_number", nullable = false, length = 16)
    private String seatNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private TransportSeatStatus status = TransportSeatStatus.AVAILABLE;

    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "price_override", precision = 14, scale = 2)
    private BigDecimal priceOverride;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;
}
