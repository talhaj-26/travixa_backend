
package com.travixa.repository.payment;

import com.travixa.entity.payment_financial.RefundRequest;
import com.travixa.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RefundRequestRepository
        extends JpaRepository<RefundRequest, Long> {

    List<RefundRequest> findAllByPayment_Booking_Customer_UserName(
            String userName);

    Optional<RefundRequest> findByIdAndPayment_Booking_Customer_UserName(
            Long id, String userName);

    Optional<RefundRequest> findByIdempotencyKey(String idempotencyKey);

    boolean existsByPayment_IdAndStatusIn(
            Long paymentId,
            List<RefundStatus> statuses);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM RefundRequest r
            WHERE r.payment.id = :paymentId
              AND r.status IN :statuses
            """)
    BigDecimal sumAmountByPaymentIdAndStatuses(
            @Param("paymentId") Long paymentId,
            @Param("statuses") List<RefundStatus> statuses);
}
