package com.travixa.repository.payment;

import com.travixa.entity.payment_financial.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    long countByCoupon_Id(Long couponId);
    long countByCoupon_IdAndUser_UserName(Long couponId, String userName);
}
