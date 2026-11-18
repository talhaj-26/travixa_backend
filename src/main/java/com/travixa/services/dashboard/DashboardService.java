
package com.travixa.services.dashboard;

import com.travixa.dto.dashboard.DashboardResponse;
import com.travixa.dto.dashboard.DashboardResponse.BookingSummary;
import com.travixa.entity.booking.Booking;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerMembership;
import com.travixa.enums.BookingStatus;
import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ListingStatus;
import com.travixa.enums.PartnerOrganizationStatus;
import com.travixa.enums.PaymentStatus;
import com.travixa.enums.TourGuideStatus;
import com.travixa.repository.booking.BookingRepository;
import com.travixa.repository.hotel.HotelRepository;
import com.travixa.repository.identity.UserRepository;
import com.travixa.repository.partner.PartnerMembershipRepository;
import com.travixa.repository.partner.PartnerOrganizationRepository;
import com.travixa.repository.payment.PaymentRepository;
import com.travixa.repository.restaurant.RestaurantRepository;
import com.travixa.repository.tour.TourGuideProfileRepository;
import com.travixa.repository.tour.TourPackageRepository;
import com.travixa.repository.transport.TransportRouteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final TourPackageRepository tourPackageRepository;
    private final TourGuideProfileRepository tourGuideProfileRepository;
    private final TransportRouteRepository transportRouteRepository;
    private final PartnerOrganizationRepository partnerOrganizationRepository;
    private final PartnerMembershipRepository partnerMembershipRepository;

    public DashboardService(
            UserRepository userRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            TourPackageRepository tourPackageRepository,
            TourGuideProfileRepository tourGuideProfileRepository,
            TransportRouteRepository transportRouteRepository,
            PartnerOrganizationRepository partnerOrganizationRepository,
            PartnerMembershipRepository partnerMembershipRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.tourGuideProfileRepository = tourGuideProfileRepository;
        this.transportRouteRepository = transportRouteRepository;
        this.partnerOrganizationRepository = partnerOrganizationRepository;
        this.partnerMembershipRepository = partnerMembershipRepository;
    }

    public DashboardResponse getOverview(Authentication authentication) {
        String username = authentication.getName();
        String role = resolveRole(authentication);

        User user = userRepository.findByUserName(username).orElse(null);
        String displayName = user == null
                ? username
                : buildDisplayName(user);

        return switch (role) {
            case "ADMIN", "SUPER_ADMIN" ->
                    platformOverview(role, displayName);
            case "CUSTOMER" ->
                    customerOverview(role, displayName, username);
            case "FINANCE_ACCOUNTANT" ->
                    financeOverview(role, displayName);
            case "HOTEL_PARTNER", "HOTEL_STAFF",
                 "RESTAURANT_PARTNER", "RESTAURANT_STAFF",
                 "TRAVEL_AGENCY_PARTNER", "TRAVEL_AGENCY_STAFF",
                 "TOUR_GUIDE", "TRANSPORT_PROVIDER", "DRIVER", "PARTNER" ->
                    partnerOverview(role, displayName, username);
            case "CONTENT_MANAGER" ->
                    contentOverview(role, displayName);
            default ->
                    limitedOverview(role, displayName);
        };
    }

    private DashboardResponse platformOverview(
            String role,
            String displayName) {

        return new DashboardResponse(
                role,
                displayName,
                "Platform-wide metrics",
                userRepository.count(),
                userRepository.countByEnabledTrue(),
                bookingRepository.count(),
                bookingRepository.countByStatus(BookingStatus.PENDING_PAYMENT),
                bookingRepository.countByStatus(BookingStatus.CONFIRMED),
                bookingRepository.countByStatus(BookingStatus.COMPLETED),
                bookingRepository.countByStatus(BookingStatus.CANCELLED),
                hotelRepository.count(),
                restaurantRepository.count(),
                tourPackageRepository.count(),
                tourGuideProfileRepository.count(),
                transportRouteRepository.count(),
                partnerOrganizationRepository.count(),
                partnerOrganizationRepository.countByStatus(
                        PartnerOrganizationStatus.PENDING),
                pendingListings(),
                paymentRepository.count(),
                paymentRepository.countByStatus(PaymentStatus.SUCCEEDED),
                paymentRepository.countByStatus(PaymentStatus.FAILED),
                safeAmount(paymentRepository.sumPaidAmountByStatusAndCurrency(
                        PaymentStatus.SUCCEEDED, CurrencyCode.BDT)),
                bookingRepository.findTop5ByOrderByPlacedAtDesc().stream()
                        .map(this::toSummary)
                        .toList()
        );
    }

    private DashboardResponse customerOverview(
            String role,
            String displayName,
            String username) {

        List<Booking> bookings =
                bookingRepository.findAllByCustomer_UserName(username);

        List<com.travixa.entity.payment_financial.Payment> payments =
                paymentRepository.findAllByBooking_Customer_UserName(username);

        long pending = countBookings(bookings, BookingStatus.PENDING_PAYMENT);
        long confirmed = countBookings(bookings, BookingStatus.CONFIRMED);
        long completed = countBookings(bookings, BookingStatus.COMPLETED);
        long cancelled = countBookings(bookings, BookingStatus.CANCELLED);

        long successfulPayments = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCEEDED)
                .count();

        long failedPayments = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();

        BigDecimal paidBdt = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCEEDED)
                .filter(p -> p.getPaidAt() != null)
                .filter(p -> p.getCurrency() == CurrencyCode.BDT)
                .map(com.travixa.entity.payment_financial.Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BookingSummary> recent = bookings.stream()
                .sorted(Comparator.comparing(
                        Booking::getPlacedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::toSummary)
                .toList();

        return new DashboardResponse(
                role, displayName, "Your personal travel activity",
                0, 0,
                bookings.size(), pending, confirmed, completed, cancelled,
                0, 0, 0, 0, 0, 0, 0, 0,
                payments.size(), successfulPayments, failedPayments,
                paidBdt, recent
        );
    }

    private DashboardResponse financeOverview(
            String role,
            String displayName) {

        return new DashboardResponse(
                role, displayName, "Financial operations",
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                paymentRepository.count(),
                paymentRepository.countByStatus(PaymentStatus.SUCCEEDED),
                paymentRepository.countByStatus(PaymentStatus.FAILED),
                safeAmount(paymentRepository.sumPaidAmountByStatusAndCurrency(
                        PaymentStatus.SUCCEEDED, CurrencyCode.BDT)),
                List.of()
        );
    }

    private DashboardResponse partnerOverview(
            String role,
            String displayName,
            String username) {

        List<PartnerMembership> memberships =
                partnerMembershipRepository.findByUser_UserNameAndActiveTrue(username);

        Set<Long> organizationIds = memberships.stream()
                .map(m -> m.getOrganization().getId())
                .collect(Collectors.toSet());

        if (organizationIds.isEmpty()) {
            return emptyOverview(
                    role, displayName,
                    "No active partner organization is linked to this account");
        }

        Collection<Long> ids = organizationIds;

        long hotels = hotelRepository.countByOrganization_IdIn(ids);
        long restaurants = restaurantRepository.countByOrganization_IdIn(ids);
        long packages = tourPackageRepository.countByOrganization_IdIn(ids);
        long guides = tourGuideProfileRepository.countByOrganization_IdIn(ids);
        long routes = transportRouteRepository.countByOrganization_IdIn(ids);

        long pending = hotelRepository.countByOrganization_IdInAndListingStatusIn(
                ids, List.of(ListingStatus.PENDING_REVIEW))
                + restaurantRepository.countByOrganization_IdInAndListingStatusIn(
                ids, List.of(ListingStatus.PENDING_REVIEW))
                + tourPackageRepository.countByOrganization_IdInAndListingStatusIn(
                ids, List.of(ListingStatus.PENDING_REVIEW));

        return new DashboardResponse(
                role, displayName, "Listings in your linked organizations",
                0, 0, 0, 0, 0, 0, 0,
                hotels, restaurants, packages, guides, routes,
                organizationIds.size(), 0, pending,
                0, 0, 0, BigDecimal.ZERO, List.of()
        );
    }

    private DashboardResponse contentOverview(
            String role,
            String displayName) {

        return new DashboardResponse(
                role, displayName, "Content review metrics",
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                pendingListings(),
                0, 0, 0, BigDecimal.ZERO, List.of()
        );
    }

    private DashboardResponse limitedOverview(
            String role,
            String displayName) {

        return emptyOverview(
                role, displayName,
                "Role-specific metrics are not available from the current backend data");
    }

    private DashboardResponse emptyOverview(
            String role,
            String displayName,
            String scope) {

        return new DashboardResponse(
                role, displayName, scope,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, BigDecimal.ZERO, List.of()
        );
    }

    private long pendingListings() {
        return hotelRepository.countByListingStatus(ListingStatus.PENDING_REVIEW)
                + restaurantRepository.countByListingStatus(ListingStatus.PENDING_REVIEW)
                + tourPackageRepository.countByListingStatus(ListingStatus.PENDING_REVIEW)
                + tourGuideProfileRepository.countByStatus(
                TourGuideStatus.PENDING_VERIFICATION);
    }

    private long countBookings(
            List<Booking> bookings,
            BookingStatus status) {

        return bookings.stream()
                .filter(b -> b.getStatus() == status)
                .count();
    }

    private BookingSummary toSummary(Booking booking) {
        return new BookingSummary(
                booking.getId(),
                booking.getReferenceNumber(),
                booking.getStatus().name(),
                booking.getTotalAmount(),
                booking.getCurrency().name(),
                booking.getPlacedAt()
        );
    }

    private String buildDisplayName(User user) {
        String first = user.getUserFirstName() == null
                ? "" : user.getUserFirstName().trim();
        String last = user.getUserLastName() == null
                ? "" : user.getUserLastName().trim();
        String fullName = (first + " " + last).trim();

        return fullName.isBlank() ? user.getUserName() : fullName;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String resolveRole(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", "").toUpperCase())
                .toList();

        List<String> priority = List.of(
                "SUPER_ADMIN", "ADMIN", "FINANCE_ACCOUNTANT",
                "CONTENT_MANAGER", "SUPPORT_EXECUTIVE",
                "HOTEL_PARTNER", "HOTEL_STAFF",
                "RESTAURANT_PARTNER", "RESTAURANT_STAFF",
                "TRAVEL_AGENCY_PARTNER", "TRAVEL_AGENCY_STAFF",
                "TOUR_GUIDE", "TRANSPORT_PROVIDER", "DRIVER",
                "PARTNER", "CUSTOMER"
        );

        return priority.stream()
                .filter(authorities::contains)
                .findFirst()
                .orElse("CUSTOMER");
    }
}
