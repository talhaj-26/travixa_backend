
package com.travixa.dto.dashboard;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        String role,
        String displayName,
        String scope,
        long totalUsers,
        long activeUsers,
        long totalBookings,
        long pendingBookings,
        long confirmedBookings,
        long completedBookings,
        long cancelledBookings,
        long totalHotels,
        long totalRestaurants,
        long totalTourPackages,
        long totalTourGuides,
        long totalTransportRoutes,
        long totalPartnerOrganizations,
        long pendingPartnerOrganizations,
        long pendingListings,
        long totalPayments,
        long successfulPayments,
        long failedPayments,
        BigDecimal paidRevenueBdt,
        List<BookingSummary> recentBookings
) {
    public record BookingSummary(
            Long id,
            String referenceNumber,
            String status,
            BigDecimal totalAmount,
            String currency,
            Instant placedAt
    ) {
    }
}
