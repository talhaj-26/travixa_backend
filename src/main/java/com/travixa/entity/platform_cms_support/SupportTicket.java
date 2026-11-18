package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.entity.partner_management.PartnerOrganization;
import com.travixa.enums.SupportTicketStatus;
import com.travixa.enums.TicketPriority;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "support_tickets", indexes = {
        @Index(name = "idx_support_ticket_status_priority", columnList = "status, priority"),
        @Index(name = "idx_support_ticket_customer", columnList = "customer_user_name")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_support_ticket_reference", columnNames = "reference_number")
})
@Getter
@Setter
@NoArgsConstructor
public class SupportTicket extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_user_name", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_name")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private PartnerOrganization organization;

    @NotBlank
    @Size(max = 40)
    @Column(name = "reference_number", nullable = false, length = 40)
    private String referenceNumber;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String subject;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TicketPriority priority = TicketPriority.NORMAL;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private SupportTicketStatus status = SupportTicketStatus.OPEN;

    private Instant resolvedAt;
}
