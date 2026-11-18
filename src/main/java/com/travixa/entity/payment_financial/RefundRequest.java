package com.travixa.entity.payment_financial;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.RefundStatus;
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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "refund_requests", indexes = {
        @Index(name = "idx_refund_requests_payment_status", columnList = "payment_id, status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_refund_reference", columnNames = "reference_number"),
        @UniqueConstraint(name = "uk_refund_idempotency", columnNames = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor
public class RefundRequest extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_user_name", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_name")
    private User processedBy;

    @NotNull
    @Size(max = 40)
    @Column(name = "reference_number", nullable = false, length = 40)
    private String referenceNumber;

    @Size(max = 128)
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyCode currency = CurrencyCode.BDT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RefundStatus status = RefundStatus.REQUESTED;

    @NotNull
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String reason;

    private Instant processedAt;
}
