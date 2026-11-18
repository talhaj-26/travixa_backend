package com.travixa.entity.restaurant;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.RestaurantTableStatus;
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
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dining_tables", uniqueConstraints = {
        @UniqueConstraint(name = "uk_dining_table_restaurant_number", columnNames = { "restaurant_id", "table_number" })
})
@Getter
@Setter
@NoArgsConstructor
public class DiningTable extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotBlank
    @Size(max = 32)
    @Column(name = "table_number", nullable = false, length = 32)
    private String tableNumber;

    @Positive
    @Column(nullable = false)
    private int capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RestaurantTableStatus status = RestaurantTableStatus.AVAILABLE;
}
