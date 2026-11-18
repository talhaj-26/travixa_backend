package com.travixa.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CouponValidateRequest(@NotBlank @Size(max = 80) String code) {
}
