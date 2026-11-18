package com.travixa.entity.payment_financial;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.CouponStatus;
import com.travixa.enums.CouponType;
import com.travixa.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
import java.time.Instant;

@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupons_status_expiry", columnList = "status, expires_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_coupon_code", columnNames = "code")
})
@Getter
@Setter
@NoArgsConstructor
public class Coupon extends BaseAuditEntity {

    @NotBlank
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponType type;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "minimum_order_amount", precision = 14, scale = 2)
    private BigDecimal minimumOrderAmount;

    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "maximum_discount_amount", precision = 14, scale = 2)
    private BigDecimal maximumDiscountAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fixed_amount_currency", length = 3)
    private CurrencyCode fixedAmountCurrency = CurrencyCode.BDT;

    @Positive
    @Column(name = "global_usage_limit")
    private Integer globalUsageLimit;

    @Positive
    @Column(name = "per_user_usage_limit")
    private Integer perUserUsageLimit = 1;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CouponStatus status = CouponStatus.DRAFT;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
