package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.RoomStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hotel_rooms", uniqueConstraints = {
        @UniqueConstraint(name = "uk_hotel_room_number", columnNames = { "hotel_id", "room_number" })
})
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @NotBlank
    @Size(max = 32)
    @Column(name = "room_number", nullable = false, length = 32)
    private String roomNumber;

    @Size(max = 40)
    @Column(name = "floor_label", length = 40)
    private String floorLabel;

    @Size(max = 120)
    @Column(name = "view_description", length = 120)
    private String viewDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RoomStatus status = RoomStatus.AVAILABLE;
}
