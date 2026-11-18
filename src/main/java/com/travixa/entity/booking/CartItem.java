package com.travixa.entity.booking;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.BookingType;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_items_cart", columnList = "cart_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_item_resource", columnNames = { "cart_id", "resource_type", "resource_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 24)
    private BookingType resourceType;

    @NotNull
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Positive
    @Column(nullable = false)
    private int quantity = 1;

    @Size(max = 1000)
    @Column(length = 1000)
    private String optionsJson;
}
