
package com.travixa.repository.payment;

import com.travixa.entity.payment_financial.Payment;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {

    boolean existsByBooking_Id(Long bookingId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findFirstByBooking_IdOrderByCreatedAtDesc(Long bookingId);

    Optional<Payment> findFirstByBooking_IdAndStatusInOrderByCreatedAtDesc(Long bookingId, List<PaymentStatus> statuses);

    List<Payment> findAllByBooking_Customer_UserName(String userName);

    Optional<Payment> findByIdAndBooking_Customer_UserName(
            Long id, String userName
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id AND p.booking.customer.userName = :userName")
    Optional<Payment> findByIdAndBooking_Customer_UserNameForUpdate(
            @Param("id") Long id, @Param("userName") String userName
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") Long id);

    long countByStatus(PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
              AND p.currency = :currency
              AND p.paidAt IS NOT NULL
            """)
    BigDecimal sumPaidAmountByStatusAndCurrency(
            @Param("status") PaymentStatus status,
            @Param("currency") CurrencyCode currency
    );
}
