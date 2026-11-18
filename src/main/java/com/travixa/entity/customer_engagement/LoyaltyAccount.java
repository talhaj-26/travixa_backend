package com.travixa.entity.customer_engagement;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
@NoArgsConstructor
public class LoyaltyAccount extends BaseAuditEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_name", nullable = false, unique = true)
    private User user;

    @PositiveOrZero
    @Column(nullable = false)
    private long pointsBalance;

    @Size(max = 40)
    @Column(length = 40)
    private String tier = "STANDARD";

    @Column(nullable = false)
    private boolean active = true;
}
