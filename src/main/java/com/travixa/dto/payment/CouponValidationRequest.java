package com.travixa.dto.payment;

import com.travixa.enums.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CouponValidationRequest(
        @NotBlank @Size(max = 40) String code,
        @NotNull @Positive BigDecimal orderAmount,
        @NotNull CurrencyCode currency
) { }
