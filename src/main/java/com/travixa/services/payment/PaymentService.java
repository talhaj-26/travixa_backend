
package com.travixa.services.payment;

import com.travixa.dto.payment.PaymentInitiationRequest;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.entity.payment_financial.PaymentAttempt;
import com.travixa.entity.payment_financial.Invoice;
import com.travixa.enums.BookingStatus;
import com.travixa.enums.InvoiceStatus;
import com.travixa.enums.PaymentStatus;
import com.travixa.repository.booking.BookingRepository;
import com.travixa.repository.payment.InvoiceRepository;
import com.travixa.repository.payment.PaymentAttemptRepository;
import com.travixa.repository.payment.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> findAllByCustomer(String userName) {
        return paymentRepository
                .findAllByBooking_Customer_UserName(userName);
    }

    public Optional<Payment> findByIdAndCustomer(
            Long id, String userName) {
        return paymentRepository
                .findByIdAndBooking_Customer_UserName(id, userName);
    }

    @Transactional
    public Payment initiate(
            PaymentInitiationRequest request,
            String userName) {

        String key = request.idempotencyKey().trim();

        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(key);

        if (existingPayment.isPresent()) {
            Payment existing = existingPayment.get();

            if (!existing.getBooking().getCustomer()
                    .getUserName().equals(userName)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key already used");
            }

            if (!existing.getBooking().getId().equals(request.bookingId())
                    || existing.getMethod() != request.method()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key reused with different request data");
            }

            return existing;
        }

        Booking booking = bookingRepository
                .findByIdAndCustomerForUpdate(request.bookingId(), userName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            if (List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS, BookingStatus.COMPLETED)
                    .contains(booking.getStatus())) {
                Optional<Payment> completedPayment = paymentRepository
                        .findFirstByBooking_IdAndStatusInOrderByCreatedAtDesc(
                                booking.getId(), List.of(PaymentStatus.SUCCEEDED));
                if (completedPayment.isPresent()) {
                    return completedPayment.get();
                }
            }
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Booking is not awaiting payment");
        }

        BigDecimal amount = booking.getTotalAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking amount must be greater than zero");
        }

        Payment activePayment = paymentRepository.findFirstByBooking_IdAndStatusInOrderByCreatedAtDesc(
                booking.getId(), List.of(PaymentStatus.INITIATED, PaymentStatus.PROCESSING)).orElse(null);
        if (activePayment != null) {
            if (activePayment.getMethod() != request.method()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A payment attempt is already open for this booking");
            }
            return activePayment;
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setReferenceNumber("PAY-" + UUID.randomUUID());
        payment.setIdempotencyKey(key);
        payment.setAmount(amount);
        payment.setCurrency(booking.getCurrency());
        payment.setMethod(request.method());
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setProviderReference(null);
        payment.setPaidAt(null);

        Payment savedPayment = paymentRepository.saveAndFlush(payment);

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setPayment(savedPayment);
        attempt.setIdempotencyKey(key);
        attempt.setStatus(PaymentStatus.INITIATED);
        attempt.setProviderReference(null);
        attempt.setProviderResultCode("GATEWAY_NOT_CONFIGURED");
        attempt.setProviderMessage(
                "Payment recorded; no gateway is configured.");
        attempt.setAttemptedAt(Instant.now());

        paymentAttemptRepository.save(attempt);

        if (request.method() == com.travixa.enums.PaymentMethod.DEMO) {
            completeDemoPayment(savedPayment, attempt);
        }

        return paymentRepository.save(savedPayment);
    }

    private void completeDemoPayment(Payment payment, PaymentAttempt attempt) {
        Instant completedAt = Instant.now();
        String demoReference = "DEMO-" + payment.getReferenceNumber();

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaidAt(completedAt);
        payment.setProviderReference(demoReference);
        attempt.setStatus(PaymentStatus.SUCCEEDED);
        attempt.setProviderReference(demoReference);
        attempt.setProviderResultCode("DEMO_SUCCESS");
        attempt.setProviderMessage("Instructor demo payment simulated; no external gateway was used.");
        attempt.setAttemptedAt(completedAt);
        paymentAttemptRepository.save(attempt);

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        markInvoicePaid(booking, payment);
    }

    /**
     * Must only be called by a trusted server-side gateway adapter
     * after independently verifying the provider's transaction result.
     *
     * Do not expose this method directly through a public controller.
     */
    @Transactional
    public Payment applyVerifiedGatewayResult(
            Long paymentId,
            VerifiedPaymentResult result) {

        if (result == null || !result.verified()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A verified gateway result is required");
        }

        if (result.status() != PaymentStatus.SUCCEEDED
                && result.status() != PaymentStatus.FAILED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Gateway result must be SUCCEEDED or FAILED");
        }

        if (result.providerReference() == null
                || result.providerReference().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider transaction reference is required");
        }

        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"));

        if (payment.getStatus() == result.status()) {
            return payment;
        }

        if (payment.getStatus() != PaymentStatus.INITIATED
                && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment is already in a terminal or unsupported state");
        }

        Booking booking = payment.getBooking();
        if (result.status() == PaymentStatus.SUCCEEDED && booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is no longer awaiting payment");
        }

        PaymentAttempt attempt = paymentAttemptRepository
                .findLatestByPaymentId(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Payment attempt not found"));

        if (result.status() == PaymentStatus.SUCCEEDED) {
            payment.setPaidAt(Instant.now());
        }

        payment.setStatus(result.status());
        payment.setProviderReference(result.providerReference());

        attempt.setStatus(result.status());
        attempt.setProviderReference(result.providerReference());
        attempt.setProviderResultCode(result.resultCode());
        attempt.setProviderMessage(result.message());
        attempt.setAttemptedAt(Instant.now());

        paymentAttemptRepository.save(attempt);
        if (result.status() == PaymentStatus.SUCCEEDED) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            markInvoicePaid(booking, payment);
        }
        return paymentRepository.save(payment);
    }

    private void markInvoicePaid(Booking booking, Payment payment) {
        Invoice invoice = invoiceRepository.findFirstByBooking_Id(booking.getId()).orElseGet(() -> {
            Invoice created = new Invoice();
            created.setBooking(booking);
            created.setInvoiceNumber("INV-" + booking.getReferenceNumber());
            created.setIssuedOn(LocalDate.now());
            created.setSubtotal(booking.getSubtotalAmount());
            created.setTaxAmount(BigDecimal.ZERO);
            created.setCurrency(booking.getCurrency());
            return created;
        });
        invoice.setTotalAmount(payment.getAmount());
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Transactional
    public void deleteById(Long id) {
        Payment payment = paymentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCEEDED
                || payment.getStatus() == PaymentStatus.PARTIALLY_REFUNDED
                || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Successful or refunded payments cannot be deleted");
        }

        paymentRepository.delete(payment);
    }
}
