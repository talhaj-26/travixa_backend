package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "support_messages", indexes = {
        @Index(name = "idx_support_messages_ticket_created", columnList = "ticket_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class SupportMessage extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_name")
    private User sender;

    @NotBlank
    @Size(max = 10000)
    @Column(nullable = false, length = 10000)
    private String message;

    @Column(name = "internal_note", nullable = false)
    private boolean internalNote;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;
}
