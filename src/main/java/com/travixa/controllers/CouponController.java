package com.travixa.controllers;

import com.travixa.dto.payment.CouponResponse;
import com.travixa.dto.payment.CouponValidationRequest;
import com.travixa.dto.payment.CouponValidationResponse;
import com.travixa.entity.payment_financial.Coupon;
import com.travixa.services.payment.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public List<CouponResponse> findAll() {
        return couponService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CouponResponse findById(@PathVariable Long id) {
        return couponService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public CouponValidationResponse validate(@Valid @RequestBody CouponValidationRequest request,
                                             Authentication authentication) {
        var applied = couponService.previewForCustomer(request.code(), authentication.getName(),
                request.orderAmount(), request.currency());
        return new CouponValidationResponse(applied.coupon().getCode(), request.orderAmount(),
                applied.discountAmount(), request.orderAmount().subtract(applied.discountAmount()), request.currency());
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(coupon.getId(), coupon.getCode(), coupon.getType(), coupon.getValue(),
                coupon.getMinimumOrderAmount(), coupon.getMaximumDiscountAmount(), coupon.getFixedAmountCurrency(),
                coupon.getGlobalUsageLimit(), coupon.getPerUserUsageLimit(), coupon.getStatus(), coupon.getStartsAt(),
                coupon.getExpiresAt());
    }
}
