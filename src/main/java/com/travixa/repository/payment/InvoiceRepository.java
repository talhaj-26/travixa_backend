package com.travixa.repository.payment;

import com.travixa.entity.payment_financial.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findFirstByBooking_Id(Long bookingId);
    List<Invoice> findAllByBooking_Customer_UserName(String userName);
    Optional<Invoice> findByIdAndBooking_Customer_UserName(Long id, String userName);
}
