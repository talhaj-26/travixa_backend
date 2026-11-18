package com.travixa.entity.restaurant;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "table_availability", indexes = {
        @Index(name = "idx_table_availability_slot", columnList = "available_on, starts_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_table_availability_slot", columnNames = { "table_id", "available_on",
                "starts_at" })
})
@Getter
@Setter
@NoArgsConstructor
public class TableAvailability extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable table;

    @NotNull
    @Column(name = "available_on", nullable = false)
    private LocalDate availableOn;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private LocalTime startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private LocalTime endsAt;

    @PositiveOrZero
    @Column(name = "available_seats", nullable = false)
    private int availableSeats;
}
