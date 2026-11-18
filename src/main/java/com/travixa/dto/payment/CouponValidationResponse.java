package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import java.math.BigDecimal;

public record CouponValidationResponse(
        String code,
        BigDecimal orderAmount,
        BigDecimal discountAmount,
        BigDecimal payableAmount,
        CurrencyCode currency
) { }
