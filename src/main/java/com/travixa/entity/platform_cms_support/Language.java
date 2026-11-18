package com.travixa.entity.platform_cms_support;

import com.travixa.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "languages", uniqueConstraints = {
        @UniqueConstraint(name = "uk_language_code", columnNames = "code")
})
@Getter
@Setter
@NoArgsConstructor
public class Language extends BaseAuditEntity {

    @NotBlank
    @Size(max = 12)
    @Column(nullable = false, length = 12)
    private String code;

    @NotBlank
    @Size(max = 80)
    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    @NotBlank
    @Size(max = 80)
    @Column(name = "native_name", nullable = false, length = 80)
    private String nativeName;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "default_language", nullable = false)
    private boolean defaultLanguage;
}
