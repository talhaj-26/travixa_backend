package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.ContactInquiryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "contact_inquiries", indexes = {
        @Index(name = "idx_contact_inquiries_status_created", columnList = "status, created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class ContactInquiry extends BaseAuditEntity {

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    private String email;

    @Size(max = 32)
    @Column(length = 32)
    private String phoneNumber;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String subject;

    @NotBlank
    @Size(max = 8000)
    @Column(nullable = false, length = 8000)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ContactInquiryStatus status = ContactInquiryStatus.NEW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_name")
    private com.travixa.entity.identity_access.User assignedTo;

    private Instant respondedAt;
}
