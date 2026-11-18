package com.travixa.entity.restaurant;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.hotel_stays.Destination;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.ListingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurants", indexes = {
        @Index(name = "idx_restaurants_org", columnList = "organization_id"),
        @Index(name = "idx_restaurants_destination", columnList = "destination_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_restaurant_org_slug", columnNames = { "organization_id", "slug" })
})
@Getter
@Setter
@NoArgsConstructor
public class Restaurant extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private PartnerOrganization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String name;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String slug;

    @Size(max = 5000)
    @Column(length = 5000)
    private String description;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String address;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String contactEmail;

    @Size(max = 32)
    @Column(length = 32)
    private String contactPhone;

    @Size(max = 120)
    @Column(length = 120)
    private String cuisineType;

    @Size(max = 500)
    @Column(name = "opening_hours", length = 500)
    private String openingHours;

    @Size(max = 2000)
    @Column(name = "cancellation_policy", length = 2000)
    private String cancellationPolicy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false, length = 24)
    private ListingStatus listingStatus = ListingStatus.DRAFT;
}
