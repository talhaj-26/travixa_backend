
package com.travixa.repository.booking;

import com.travixa.entity.booking.Booking;
import com.travixa.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository
        extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    List<Booking> findAllByCustomer_UserName(String userName);

    Optional<Booking> findByIdAndCustomer_UserName(
            Long id,
            String userName
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id and b.customer.userName = :userName")
    Optional<Booking> findByIdAndCustomerForUpdate(@Param("id") Long id, @Param("userName") String userName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    long countByStatus(BookingStatus status);

    List<Booking> findTop5ByOrderByPlacedAtDesc();

}
