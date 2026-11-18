
package com.travixa.repository.payment;

import com.travixa.entity.payment_financial.PaymentAttempt;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentAttemptRepository
        extends JpaRepository<PaymentAttempt, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a
            FROM PaymentAttempt a
            WHERE a.payment.id = :paymentId
            ORDER BY a.attemptedAt DESC
            """)
    java.util.List<PaymentAttempt> findAllByPaymentIdForUpdate(
            @Param("paymentId") Long paymentId);

    default Optional<PaymentAttempt> findLatestByPaymentId(Long paymentId) {
        return findAllByPaymentIdForUpdate(paymentId)
                .stream()
                .findFirst();
    }
}
