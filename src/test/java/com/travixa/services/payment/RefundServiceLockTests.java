package com.travixa.services.payment;

import com.travixa.dto.payment.RefundCreateRequest;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.entity.payment_financial.RefundRequest;
import com.travixa.enums.BookingStatus;
import com.travixa.enums.PaymentStatus;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.payment.InvoiceRepository;
import com.travixa.repository.payment.PaymentRepository;
import com.travixa.repository.payment.RefundRequestRepository;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefundServiceLockTests {

    @Test
    void locksPaymentBeforeCheckingReservedRefundAmounts() {
        List<String> calls = new ArrayList<>();
        Fixture fixture = fixture(BigDecimal.ZERO, calls);

        RefundRequest result = fixture.service().create(
                new RefundCreateRequest(7L, new BigDecimal("40.00"), "Change of plans", "refund-1"),
                "customer-a");

        assertEquals(new BigDecimal("40.00"), result.getAmount());
        assertTrue(calls.indexOf("payment-lock") >= 0);
        assertTrue(calls.indexOf("payment-lock") < calls.indexOf("refund-sum"));
        assertTrue(calls.contains("refund-save"));
    }

    @Test
    void rejectsRefundAmountThatExceedsUnreservedPaymentBalance() {
        List<String> calls = new ArrayList<>();
        Fixture fixture = fixture(new BigDecimal("75.00"), calls);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> fixture.service().create(
                        new RefundCreateRequest(7L, new BigDecimal("30.00"), "Refund", "refund-2"),
                        "customer-a"));

        assertEquals(400, error.getStatusCode().value());
        assertTrue(calls.contains("payment-lock"));
        assertTrue(calls.contains("refund-sum"));
        assertTrue(!calls.contains("refund-save"));
    }

    @Test
    void ownerScopedPaymentLookupUsesPessimisticWriteLock() throws Exception {
        Method method = PaymentRepository.class.getMethod(
                "findByIdAndBooking_Customer_UserNameForUpdate", Long.class, String.class);

        assertEquals(LockModeType.PESSIMISTIC_WRITE, method.getAnnotation(Lock.class).value());
        assertTrue(method.getAnnotation(Query.class).value().contains("booking.customer.userName"));
    }

    private Fixture fixture(BigDecimal existingRefunds, List<String> calls) {
        User customer = new User();
        customer.setUserName("customer-a");
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStatus(BookingStatus.CONFIRMED);
        Payment payment = new Payment();
        payment.setId(7L);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(PaymentStatus.SUCCEEDED);

        PaymentRepository payments = proxy(PaymentRepository.class, (self, method, args) -> switch (method.getName()) {
            case "findByIdempotencyKey" -> Optional.empty();
            case "findByIdAndBooking_Customer_UserNameForUpdate" -> {
                calls.add("payment-lock");
                yield Optional.of(payment);
            }
            default -> defaultValue(method.getReturnType());
        });
        RefundRequestRepository refunds = proxy(RefundRequestRepository.class, (self, method, args) -> switch (method.getName()) {
            case "findByIdempotencyKey" -> Optional.empty();
            case "sumAmountByPaymentIdAndStatuses" -> {
                calls.add("refund-sum");
                yield existingRefunds;
            }
            case "save" -> {
                calls.add("refund-save");
                yield args[0];
            }
            default -> defaultValue(method.getReturnType());
        });
        UserRepository users = proxy(UserRepository.class, (self, method, args) ->
                method.getName().equals("findByUserName") ? Optional.of(customer) : defaultValue(method.getReturnType()));
        InvoiceRepository invoices = proxy(InvoiceRepository.class,
                (self, method, args) -> defaultValue(method.getReturnType()));

        return new Fixture(new RefundService(refunds, payments, users, invoices));
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> repositoryType, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(repositoryType.getClassLoader(), new Class<?>[]{repositoryType},
                (self, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" -> self == args[0];
                            case "hashCode" -> System.identityHashCode(self);
                            case "toString" -> repositoryType.getSimpleName() + " test proxy";
                            default -> null;
                        };
                    }
                    return handler.invoke(self, method, args);
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        return 0;
    }

    private record Fixture(RefundService service) { }
}
