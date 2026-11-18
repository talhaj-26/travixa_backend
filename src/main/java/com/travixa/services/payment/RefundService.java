
package com.travixa.services.payment;

import com.travixa.dto.payment.RefundCreateRequest;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.entity.payment_financial.RefundRequest;
import com.travixa.enums.PaymentStatus;
import com.travixa.enums.PaymentMethod;
import com.travixa.enums.InvoiceStatus;
import com.travixa.enums.RefundStatus;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.payment.PaymentRepository;
import com.travixa.repository.payment.InvoiceRepository;
import com.travixa.repository.payment.RefundRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RefundService {

    private static final List<RefundStatus> ACTIVE_REFUND_STATUSES =
            List.of(
                    RefundStatus.REQUESTED,
                    RefundStatus.APPROVED,
                    RefundStatus.PROCESSING,
                    RefundStatus.COMPLETED
            );

    private final RefundRequestRepository refundRequestRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;

    public RefundService(
            RefundRequestRepository refundRequestRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            InvoiceRepository invoiceRepository) {
        this.refundRequestRepository = refundRequestRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<RefundRequest> findAll() {
        return refundRequestRepository.findAll();
    }

    public Optional<RefundRequest> findById(Long id) {
        return refundRequestRepository.findById(id);
    }

    public List<RefundRequest> findAllByCustomer(String userName) {
        return refundRequestRepository
                .findAllByPayment_Booking_Customer_UserName(userName);
    }

    public Optional<RefundRequest> findByIdAndCustomer(
            Long id, String userName) {
        return refundRequestRepository
                .findByIdAndPayment_Booking_Customer_UserName(id, userName);
    }

    @Transactional
    public RefundRequest create(
            RefundCreateRequest request,
            String userName) {

        String key = request.idempotencyKey().trim();
        String reason = request.reason().trim();

        RefundRequest previous = refundRequestRepository.findByIdempotencyKey(key).orElse(null);
        if (previous != null) return returnExisting(previous, request, reason, userName);

        Payment payment = paymentRepository
                .findByIdAndBooking_Customer_UserNameForUpdate(request.paymentId(), userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"));

        // A concurrent retry with the same key may have committed while this
        // request waited for the payment row lock.
        previous = refundRequestRepository.findByIdempotencyKey(key).orElse(null);
        if (previous != null) return returnExisting(previous, request, reason, userName);

        if (payment.getStatus() != PaymentStatus.SUCCEEDED
                && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only successful payments can be refunded");
        }

        BigDecimal alreadyRequested =
                refundRequestRepository.sumAmountByPaymentIdAndStatuses(
                        payment.getId(), ACTIVE_REFUND_STATUSES);

        if (alreadyRequested == null) {
            alreadyRequested = BigDecimal.ZERO;
        }

        BigDecimal remaining = payment.getAmount().subtract(alreadyRequested);

        if (request.amount().compareTo(remaining) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refund amount exceeds the remaining refundable amount");
        }

        User customer = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        RefundRequest refund = new RefundRequest();
        refund.setPayment(payment);
        refund.setRequestedBy(customer);
        refund.setReferenceNumber("RFD-" + UUID.randomUUID());
        refund.setIdempotencyKey(key);
        refund.setAmount(request.amount());
        refund.setCurrency(payment.getCurrency());
        refund.setStatus(RefundStatus.REQUESTED);
        refund.setReason(reason);
        refund.setProcessedAt(null);

        return refundRequestRepository.save(refund);
    }

    private RefundRequest returnExisting(RefundRequest previous, RefundCreateRequest request,
            String reason, String userName) {
        if (!previous.getPayment().getBooking().getCustomer().getUserName().equals(userName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key already used");
        }
        if (!previous.getPayment().getId().equals(request.paymentId())
                || previous.getAmount().compareTo(request.amount()) != 0
                || !previous.getReason().equals(reason)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Idempotency key reused with different request data");
        }
        return previous;
    }

    @Transactional
    public RefundRequest review(
            Long refundId,
            RefundStatus requestedStatus,
            String reviewerUsername) {

        if (requestedStatus != RefundStatus.APPROVED
                && requestedStatus != RefundStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review status must be APPROVED or REJECTED");
        }

        RefundRequest refund = refundRequestRepository.findById(refundId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Refund request not found"));

        if (refund.getStatus() != RefundStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only REQUESTED refunds can be reviewed");
        }

        User reviewer = userRepository.findByUserName(reviewerUsername)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reviewer not found"));

        refund.setStatus(requestedStatus);
        refund.setProcessedBy(reviewer);
        refund.setProcessedAt(Instant.now());

        return refundRequestRepository.save(refund);
    }

    @Transactional
    public RefundRequest completeDemo(Long refundId) {
        RefundRequest refund = refundRequestRepository.findById(refundId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refund request not found"));
        if (refund.getStatus() == RefundStatus.COMPLETED) return refund;
        if (refund.getStatus() != RefundStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only approved refunds can be completed");
        }

        Payment payment = paymentRepository.findByIdForUpdate(refund.getPayment().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (payment.getMethod() != PaymentMethod.DEMO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only demo payments can use simulated refund completion");
        }

        BigDecimal completedBefore = refundRequestRepository.sumAmountByPaymentIdAndStatuses(
                payment.getId(), List.of(RefundStatus.COMPLETED));
        if (completedBefore == null) completedBefore = BigDecimal.ZERO;
        BigDecimal refundedTotal = completedBefore.add(refund.getAmount());
        if (refundedTotal.compareTo(payment.getAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Completed refunds exceed the payment amount");
        }

        refund.setStatus(RefundStatus.COMPLETED);
        refund.setProcessedAt(Instant.now());
        if (refundedTotal.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.getBooking().setStatus(com.travixa.enums.BookingStatus.REFUNDED);
            invoiceRepository.findFirstByBooking_Id(payment.getBooking().getId()).ifPresent(invoice -> {
                invoice.setStatus(InvoiceStatus.VOID);
                invoiceRepository.save(invoice);
            });
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        paymentRepository.save(payment);
        return refundRequestRepository.save(refund);
    }

    @Transactional
    public RefundRequest save(RefundRequest refundRequest) {
        return refundRequestRepository.save(refundRequest);
    }
}
