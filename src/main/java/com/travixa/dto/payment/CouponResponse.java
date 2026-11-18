package com.travixa.dto.payment;

import com.travixa.enums.CouponStatus;
import com.travixa.enums.CouponType;
import com.travixa.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        CouponType type,
        BigDecimal value,
        BigDecimal minimumOrderAmount,
        BigDecimal maximumDiscountAmount,
        CurrencyCode fixedAmountCurrency,
        Integer globalUsageLimit,
        Integer perUserUsageLimit,
        CouponStatus status,
        Instant startsAt,
        Instant expiresAt) {
}
