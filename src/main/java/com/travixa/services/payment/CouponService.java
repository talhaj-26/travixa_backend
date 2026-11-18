package com.travixa.services.payment;

import com.travixa.entity.payment_financial.Coupon;
import com.travixa.repository.payment.CouponRepository;
import com.travixa.repository.payment.CouponRedemptionRepository;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.payment_financial.CouponRedemption;
import com.travixa.enums.CouponStatus;
import com.travixa.enums.CouponType;
import com.travixa.enums.CurrencyCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;

    public CouponService(CouponRepository couponRepository, CouponRedemptionRepository redemptionRepository) {
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
    }

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id);
    }

    @Transactional
    public Coupon save(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Transactional
    public AppliedCoupon validateForBooking(String code, String userName, BigDecimal subtotal, CurrencyCode currency) {
        if (code == null || code.isBlank()) return null;
        Coupon coupon = couponRepository.findByCodeForUpdate(code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is invalid"));
        return calculate(coupon, userName, subtotal, currency, true);
    }

    private AppliedCoupon calculate(Coupon coupon, String userName, BigDecimal subtotal,
                                    CurrencyCode currency, boolean checkUsage) {
        Instant now = Instant.now();
        if (coupon.getStatus() != CouponStatus.ACTIVE || now.isBefore(coupon.getStartsAt()) || now.isAfter(coupon.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is not active");
        }
        if (checkUsage && coupon.getGlobalUsageLimit() != null && redemptionRepository.countByCoupon_Id(coupon.getId()) >= coupon.getGlobalUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code usage limit has been reached");
        }
        if (checkUsage && coupon.getPerUserUsageLimit() != null
                && redemptionRepository.countByCoupon_IdAndUser_UserName(coupon.getId(), userName) >= coupon.getPerUserUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already used this promo code");
        }
        if (coupon.getMinimumOrderAmount() != null && subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking total does not meet the promo minimum");
        }
        BigDecimal discount;
        if (coupon.getType() == CouponType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            if (coupon.getFixedAmountCurrency() != currency) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code currency does not match the booking");
            }
            discount = coupon.getValue().setScale(2, RoundingMode.HALF_UP);
        }
        if (coupon.getMaximumDiscountAmount() != null) discount = discount.min(coupon.getMaximumDiscountAmount());
        discount = discount.min(subtotal);
        if (discount.signum() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code does not apply to this booking");
        return new AppliedCoupon(coupon, discount);
    }

    @Transactional(readOnly = true)
    public AppliedCoupon previewForCustomer(String code, String userName, BigDecimal subtotal, CurrencyCode currency) {
        // Preview is informational only; BookingService locks and recalculates this coupon at submission.
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Promo code is invalid"));
        return calculate(coupon, userName, subtotal, currency, true);
    }

    @Transactional
    public void recordRedemption(AppliedCoupon applied, User user, Booking booking, CurrencyCode currency) {
        if (applied == null) return;
        CouponRedemption redemption = new CouponRedemption();
        redemption.setCoupon(applied.coupon());
        redemption.setUser(user);
        redemption.setBooking(booking);
        redemption.setDiscountAmount(applied.discountAmount());
        redemption.setCurrency(currency);
        redemption.setRedeemedAt(Instant.now());
        redemptionRepository.save(redemption);
    }

    public record AppliedCoupon(Coupon coupon, BigDecimal discountAmount) { }
}
