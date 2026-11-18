package com.travixa.entity.partner_management;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.PartnerApplicationStatus;
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
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "partner_approvals", indexes = {
        @Index(name = "idx_partner_approval_application", columnList = "application_id, decided_at")
})
@Getter
@Setter
@NoArgsConstructor
public class PartnerApproval extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private PartnerApplication application;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_user_name", nullable = false)
    private User reviewer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PartnerApplicationStatus decision;

    @Size(max = 2000)
    @Column(length = 2000)
    private String notes;

    @NotNull
    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;
}
