package com.travixa.entity.booking;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.BookingStatus;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_customer_created", columnList = "customer_user_name, created_at"),
        @Index(name = "idx_bookings_status_created", columnList = "status, created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_booking_reference", columnNames = "reference_number"),
        @UniqueConstraint(name = "uk_booking_idempotency_key", columnNames = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor
public class Booking extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_name", nullable = false)
    private User customer;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    @Size(max = 40)
    @Column(name = "reference_number", nullable = false, length = 40)
    private String referenceNumber;

    @Size(max = 128)
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Column(name = "service_start_date")
    private LocalDate serviceStartDate;

    @Column(name = "service_end_date")
    private LocalDate serviceEndDate;

    @Size(max = 2000)
    @Column(name = "special_request", length = 2000)
    private String specialRequest;

    @Size(max = 2000)
    @Column(name = "personal_touch", length = 2000)
    private String personalTouch;

    @jakarta.validation.constraints.Positive
    @Column(name = "guest_count", nullable = false, columnDefinition = "integer default 1")
    private int guestCount = 1;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "subtotal_amount", nullable = false, precision = 14, scale = 2, columnDefinition = "numeric(14,2) default 0")
    private BigDecimal subtotalAmount = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "discount_amount", nullable = false, precision = 14, scale = 2, columnDefinition = "numeric(14,2) default 0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Size(max = 40)
    @Column(name = "coupon_code", length = 40)
    private String couponCode;
}
