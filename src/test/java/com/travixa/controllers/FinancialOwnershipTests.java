package com.travixa.controllers;

import com.travixa.dto.user.UserCreateRequest;
import com.travixa.repository.booking.CartItemRepository;
import com.travixa.repository.booking.CartRepository;
import com.travixa.repository.identity.RefreshTokenRepository;
import com.travixa.repository.identity.RoleRepository;
import com.travixa.services.authentication_identity.UserService;
import com.travixa.services.booking.BookingService;
import com.travixa.services.payment.PaymentService;
import com.travixa.services.payment.RefundService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FinancialOwnershipTests {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    void bookingReadsUseCustomerScopedServicesAndHideOtherCustomersBookings() {
        BookingService service = mock(BookingService.class);
        BookingController controller = new BookingController(
                service,
                cartRepository,
                cartItemRepository
        );
        Authentication customer = customer("alice");

        controller.findAll(customer);
        when(service.findByIdAndCustomer(41L, "alice")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> controller.findById(41L, customer));

        assertEquals(404, exception.getStatusCode().value());
        verify(service).findAllByCustomer("alice");
        verify(service, never()).findAll();
        verify(service).findByIdAndCustomer(41L, "alice");
        verify(service, never()).findById(41L);
    }

    @Test
    void paymentReadsUseCustomerScopeUnlessCallerIsFinancialAdministrator() {
        PaymentService service = mock(PaymentService.class);
        PaymentController controller = new PaymentController(service);

        controller.findAll(customer("alice"));
        controller.findAll(authentication("finance", "FINANCE_ACCOUNTANT"));

        verify(service).findAllByCustomer("alice");
        verify(service).findAll();
    }

    @Test
    void refundReadsUseCustomerScopedServicesAndHideOtherCustomersRefunds() {
        RefundService service = mock(RefundService.class);
        RefundController controller = new RefundController(service);
        Authentication customer = customer("alice");

        controller.findAll(customer);
        when(service.findByIdAndCustomer(52L, "alice")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> controller.findById(52L, customer));

        assertEquals(404, exception.getStatusCode().value());
        verify(service).findAllByCustomer("alice");
        verify(service, never()).findAll();
        verify(service).findByIdAndCustomer(52L, "alice");
        verify(service, never()).findById(52L);
    }

    @Test
    void adminCannotGrantAdministratorRoles() {
        UserService userService = mock(UserService.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserController controller = new UserController(
                userService,
                roleRepository,
                passwordEncoder,
                refreshTokenRepository
        );
        UserCreateRequest request = new UserCreateRequest(
                "new-admin", null, null, "new-admin@example.com", "strong-password", Set.of("ADMIN"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.create(request, authentication("admin", "ADMIN")));

        assertEquals(403, exception.getStatusCode().value());
        verifyNoInteractions(userService, roleRepository, passwordEncoder);
    }

    private Authentication customer(String username) {
        return authentication(username, "CUSTOMER");
    }

    private Authentication authentication(String username, String authority) {
        return UsernamePasswordAuthenticationToken.authenticated(
                username, null, List.of(new SimpleGrantedAuthority(authority)));
    }
}
