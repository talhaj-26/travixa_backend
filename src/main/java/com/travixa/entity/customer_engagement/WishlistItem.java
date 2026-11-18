package com.travixa.entity.customer_engagement;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.WishlistType;
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
@Table(name = "wishlist_items", indexes = {
        @Index(name = "idx_wishlist_user", columnList = "user_name")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_wishlist_user_target", columnNames = { "user_name", "item_type", "target_id" })
})
@Getter
@Setter
@NoArgsConstructor
public class WishlistItem extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 32)
    private WishlistType itemType;

    @NotNull
    @Positive
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Size(max = 500)
    @Column(length = 500)
    private String note;
}
