package com.travixa.entity.restaurant;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.RestaurantReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "restaurant_reservations", indexes = {
        @Index(name = "idx_restaurant_reservation_org_start", columnList = "restaurant_id, starts_at"),
        @Index(name = "idx_restaurant_reservation_customer", columnList = "customer_user_name")
})
@Getter
@Setter
@NoArgsConstructor
public class RestaurantReservation extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private DiningTable table;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_name", nullable = false)
    private User customer;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @NotNull
    @Positive
    @Column(name = "guest_count", nullable = false)
    private int guestCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RestaurantReservationStatus status = RestaurantReservationStatus.PENDING;

    @Size(max = 1000)
    @Column(name = "special_requests", length = 1000)
    private String specialRequests;
}
