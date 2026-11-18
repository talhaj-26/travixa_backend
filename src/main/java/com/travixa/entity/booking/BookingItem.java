package com.travixa.entity.booking;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.entity.tour_guide.PackageDeparture;
import com.travixa.enums.BookingType;
import com.travixa.enums.CurrencyCode;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_items", indexes = {
        @Index(name = "idx_booking_items_booking", columnList = "booking_id"),
        @Index(name = "idx_booking_items_org_resource", columnList = "organization_id, resource_type, resource_id")
})
@Getter
@Setter
@NoArgsConstructor
public class BookingItem extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 24)
    private BookingType resourceType;

    @NotNull
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @NotBlank
    @Size(max = 240)
    @Column(name = "description_snapshot", nullable = false, length = 240)
    private String descriptionSnapshot;

    @Positive
    @Column(nullable = false)
    private int quantity;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_departure_id")
    private PackageDeparture tourDeparture;
}
