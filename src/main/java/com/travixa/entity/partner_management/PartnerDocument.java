package com.travixa.entity.partner_management;

import com.travixa.common.BaseAuditEntity;
import com.travixa.entity.identity_access.User;
import com.travixa.enums.PartnerDocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partner_documents", indexes = {
        @Index(name = "idx_partner_document_application", columnList = "application_id, status")
})
@Getter
@Setter
@NoArgsConstructor
public class PartnerDocument extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private PartnerApplication application;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by_user_name", nullable = false)
    private User uploadedBy;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String documentType;

    @NotBlank
    @Size(max = 500)
    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Size(max = 255)
    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Size(max = 100)
    @Column(name = "content_type", length = 100)
    private String contentType;

    @Size(max = 64)
    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PartnerDocumentStatus status = PartnerDocumentStatus.UPLOADED;
}
