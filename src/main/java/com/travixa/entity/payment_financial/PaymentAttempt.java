package com.travixa.entity.payment_financial;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.PaymentStatus;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "payment_attempts", indexes = {
        @Index(name = "idx_payment_attempts_payment_created", columnList = "payment_id, created_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_attempt_idempotency", columnNames = "idempotency_key")
})
@Getter
@Setter
@NoArgsConstructor
public class PaymentAttempt extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Size(max = 128)
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Size(max = 120)
    @Column(name = "provider_reference", length = 120)
    private String providerReference;

    @Size(max = 80)
    @Column(name = "provider_result_code", length = 80)
    private String providerResultCode;

    @Size(max = 500)
    @Column(name = "provider_message", length = 500)
    private String providerMessage;

    @NotNull
    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;
}
