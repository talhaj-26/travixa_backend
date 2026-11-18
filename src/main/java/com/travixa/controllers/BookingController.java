
package com.travixa.controllers;

import com.travixa.dto.booking.BookingCreateRequest;
import com.travixa.dto.booking.BookingItemRequest;
import com.travixa.dto.booking.BookingResponse;
import com.travixa.dto.booking.CartCheckoutRequest;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.booking.Cart;
import com.travixa.entity.booking.CartItem;
import com.travixa.enums.CartStatus;
import com.travixa.mapper.BookingMapper;
import com.travixa.repository.booking.CartItemRepository;
import com.travixa.repository.booking.CartRepository;
import com.travixa.services.booking.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public BookingController(
            BookingService bookingService,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository
    ) {
        this.bookingService = bookingService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @PostMapping
    public BookingResponse createDirect(
            @Valid @RequestBody BookingCreateRequest request,
            Authentication authentication
    ) {
        Booking booking = bookingService.create(request, authentication.getName());
        return toResponse(booking);
    }

    @PostMapping("/from-cart")
    @Transactional
    public BookingResponse createFromCart(
            @Valid @RequestBody CartCheckoutRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        // Retry with the same key returns the previous booking.
        Booking existing = bookingService
                .findByIdempotencyKey(request.idempotencyKey())
                .orElse(null);

        if (existing != null) {
            if (existing.getCustomer() == null
                    || !username.equals(
                    existing.getCustomer().getUserName())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key already used"
                );
            }

            return toResponse(existing);
        }

        Cart cart = cartRepository.findByUser_UserName(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cart is not active");
        }

        List<CartItem> cartItems =
                cartItemRepository.findAllByCart_Id(cart.getId());

        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        List<BookingItemRequest> bookingItems = cartItems.stream()
                .map(item -> new BookingItemRequest(
                        item.getResourceType(),
                        item.getResourceId(),
                        item.getQuantity(),
                        departureId(item)
                ))
                .toList();

        BookingCreateRequest bookingRequest = new BookingCreateRequest(
                request.idempotencyKey(),
                bookingItems,
                request.guests(),
                request.serviceStartDate(),
                request.serviceEndDate(),
                request.specialRequest(),
                request.guestCount(),
                request.couponCode(),
                request.personalTouch()
        );

        Booking booking = bookingService.create(bookingRequest, username);

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return toResponse(booking);
    }

    private Long departureId(CartItem item) {
        if (item.getResourceType() != com.travixa.enums.BookingType.TOUR
                || item.getOptionsJson() == null || item.getOptionsJson().isBlank()) return null;
        try {
            JsonNode value = objectMapper.readTree(item.getOptionsJson()).get("departureId");
            if (value == null || value.isNull()) return null;
            if (value.canConvertToLong() && value.asLong() > 0) return value.asLong();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid package departure ID");
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid cart item options", ex);
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<BookingResponse> findAll(
            Authentication authentication
    ) {
        List<Booking> bookings =
                hasAuthority(authentication, "ADMIN", "SUPER_ADMIN")
                        ? bookingService.findAll()
                        : bookingService.findAllByCustomer(
                        authentication.getName());

        return bookings.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public BookingResponse findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Booking booking =
                hasAuthority(authentication, "ADMIN", "SUPER_ADMIN")
                        ? bookingService.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"))
                        : bookingService.findByIdAndCustomer(
                                id, authentication.getName())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"));

        return toResponse(booking);
    }

    @PatchMapping("/{id}/cancel")
    @Transactional
    public BookingResponse cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        Booking booking = hasAuthority(authentication, "ADMIN", "SUPER_ADMIN")
                ? bookingService.cancelByAdmin(id)
                : bookingService.cancelByCustomer(id, authentication.getName());

        return toResponse(booking);
    }

    private boolean hasAuthority(
            Authentication authentication,
            String... authorities
    ) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> Arrays.asList(authorities)
                        .contains(granted.getAuthority()));
    }

    private BookingResponse toResponse(Booking booking) {
        List<com.travixa.entity.booking.BookingItem> items = bookingService.findItemsByBookingId(booking.getId());
        return BookingMapper.toResponse(
                booking,
                bookingService.paymentStatusForBooking(booking.getId()),
                items,
                bookingService.findGuestsByBookingId(booking.getId()),
                bookingService.hotelDetailsByBookingItem(items)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        bookingService.deleteById(id, authentication.getName());
    }
}
