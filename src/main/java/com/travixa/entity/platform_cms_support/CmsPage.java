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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cms_pages", indexes = {
        @Index(name = "idx_cms_pages_status", columnList = "status, published_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_cms_page_slug_locale", columnNames = { "slug", "locale_code" })
})
@Getter
@Setter
@NoArgsConstructor
public class CmsPage extends BaseAuditEntity {

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String slug;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 30000)
    @Column(nullable = false, length = 30000)
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "locale_code", referencedColumnName = "code", nullable = false)
    private Language language;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private ContentStatus status = ContentStatus.DRAFT;

    private Instant publishedAt;
}
