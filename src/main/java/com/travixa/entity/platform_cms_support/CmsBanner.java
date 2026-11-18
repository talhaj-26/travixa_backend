package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import com.travixa.enums.ContentStatus;
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
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cms_banners", indexes = {
        @Index(name = "idx_cms_banners_status_window", columnList = "status, starts_at, ends_at")
})
@Getter
@Setter
@NoArgsConstructor
public class CmsBanner extends BaseAuditEntity {

    @NotBlank
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String title;

    @NotBlank
    @Size(max = 500)
    @Column(name = "image_storage_key", nullable = false, length = 500)
    private String imageStorageKey;

    @Size(max = 1000)
    @Column(name = "target_url", length = 1000)
    private String targetUrl;

    @PositiveOrZero
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ContentStatus status = ContentStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locale_code", referencedColumnName = "code")
    private Language language;

    private Instant startsAt;

    private Instant endsAt;
}
