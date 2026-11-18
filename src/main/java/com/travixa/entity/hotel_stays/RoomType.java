package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.BedType;
import com.travixa.enums.ListingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types", uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_type_hotel_name", columnNames = { "hotel_id", "name" })
})
@Getter
@Setter
@NoArgsConstructor
public class RoomType extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private com.travixa.enums.RoomType category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type", nullable = false, length = 24)
    private BedType bedType;

    @Positive
    @Column(name = "max_occupancy", nullable = false)
    private int maxOccupancy;

    @PositiveOrZero
    @Column(name = "size_square_meters", precision = 8, scale = 2)
    private BigDecimal sizeSquareMeters;

    @Column(name = "air_conditioned")
    private Boolean airConditioned;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "room_type_amenities", joinColumns = @JoinColumn(name = "room_type_id"))
    @OrderColumn(name = "display_order")
    @Column(name = "amenity", nullable = false, length = 80)
    private List<String> amenities = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_status", nullable = false, length = 24)
    private ListingStatus listingStatus = ListingStatus.DRAFT;
}
