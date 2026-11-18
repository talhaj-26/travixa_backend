package com.travixa.entity.hotel_stays;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "room_availability", indexes = {
        @Index(name = "idx_room_availability_date", columnList = "available_on")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_room_availability_room_date", columnNames = { "room_id", "available_on" })
})
@Getter
@Setter
@NoArgsConstructor
public class RoomAvailability extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull
    @Column(name = "available_on", nullable = false)
    private LocalDate availableOn;

    @PositiveOrZero
    @Max(1)
    @Column(name = "available_units", nullable = false)
    private int availableUnits;
}
