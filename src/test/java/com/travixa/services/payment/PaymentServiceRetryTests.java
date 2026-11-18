package com.travixa.services.payment;

import com.travixa.dto.payment.PaymentInitiationRequest;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.enums.BookingStatus;
import com.travixa.enums.PaymentMethod;
import com.travixa.enums.PaymentStatus;
import com.travixa.repository.booking.BookingRepository;
import com.travixa.repository.payment.InvoiceRepository;
import com.travixa.repository.payment.PaymentAttemptRepository;
import com.travixa.repository.payment.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PaymentServiceRetryTests {

    @Test
    void retryAfterSuccessfulPaymentReturnsTheExistingPayment() {
        List<String> calls = new ArrayList<>();
        User customer = new User();
        customer.setUserName("customer-a");

        Booking booking = new Booking();
        booking.setId(18L);
        booking.setCustomer(customer);
        booking.setStatus(BookingStatus.CONFIRMED);

        Payment successfulPayment = new Payment();
        successfulPayment.setBooking(booking);
        successfulPayment.setMethod(PaymentMethod.DEMO);
        successfulPayment.setStatus(PaymentStatus.SUCCEEDED);

        PaymentRepository payments = proxy(PaymentRepository.class, (self, method, args) -> switch (method.getName()) {
            case "findByIdempotencyKey" -> Optional.empty();
            case "findFirstByBooking_IdAndStatusInOrderByCreatedAtDesc" -> {
                calls.add("successful-payment");
                assertEquals(List.of(PaymentStatus.SUCCEEDED), args[1]);
                yield Optional.of(successfulPayment);
            }
            default -> defaultValue(method.getReturnType());
        });
        BookingRepository bookings = proxy(BookingRepository.class, (self, method, args) -> {
            if (method.getName().equals("findByIdAndCustomerForUpdate")) {
                calls.add("booking-lock");
                return Optional.of(booking);
            }
            return defaultValue(method.getReturnType());
        });

        PaymentService service = new PaymentService(
                payments,
                bookings,
                proxy(PaymentAttemptRepository.class, (self, method, args) -> defaultValue(method.getReturnType())),
                proxy(InvoiceRepository.class, (self, method, args) -> defaultValue(method.getReturnType())));

        Payment result = service.initiate(
                new PaymentInitiationRequest(18L, PaymentMethod.DEMO, "retry-key"), "customer-a");

        assertSame(successfulPayment, result);
        assertEquals(List.of("booking-lock", "successful-payment"), calls);
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
}
