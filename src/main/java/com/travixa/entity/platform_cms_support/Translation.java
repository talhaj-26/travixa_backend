package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Entity
@Table(name = "translations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_translation_language_key", columnNames = { "language_code", "translation_key" })
})
@Getter
@Setter
@NoArgsConstructor
public class Translation extends BaseAuditEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_code", referencedColumnName = "code", nullable = false)
    private Language language;

    @NotBlank
    @Size(max = 180)
    @Column(name = "translation_key", nullable = false, length = 180)
    private String translationKey;

    @NotBlank
    @Size(max = 4000)
    @Column(nullable = false, length = 4000)
    private String value;
}
