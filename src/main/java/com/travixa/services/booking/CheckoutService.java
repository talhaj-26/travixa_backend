package com.travixa.services.booking;

import com.travixa.entity.booking.Booking;
import com.travixa.entity.booking.Cart;
import com.travixa.entity.payment_financial.Payment;
import com.travixa.dto.booking.CheckoutRequest;
import com.travixa.dto.booking.CheckoutResponse;
import com.travixa.dto.payment.PaymentInitiationRequest;
import com.travixa.repository.booking.BookingRepository;
import com.travixa.repository.booking.CartRepository;
import com.travixa.services.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CheckoutService {

    private final CartRepository cartRepository;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;

    public CheckoutService(CartRepository cartRepository, BookingRepository bookingRepository,
            PaymentService paymentService) {
        this.cartRepository = cartRepository;
        this.bookingRepository = bookingRepository;
        this.paymentService = paymentService;
    }

    public Optional<Cart> findCartById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    @Transactional
    public CheckoutResponse initiatePayment(CheckoutRequest request, String username) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (booking.getCustomer() == null || !booking.getCustomer().getUserName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking does not belong to the current user");
        }

        Payment payment = paymentService.initiate(
                new PaymentInitiationRequest(booking.getId(), request.paymentMethod(), request.idempotencyKey()),
                username);
        return new CheckoutResponse(booking.getId(), payment.getId(), booking.getStatus(), payment.getStatus(),
                payment.getAmount(), payment.getCurrency());
    }
}
