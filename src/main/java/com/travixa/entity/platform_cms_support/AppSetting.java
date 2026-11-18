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
@Table(name = "app_settings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_app_setting_key", columnNames = "setting_key")
})
@Getter
@Setter
@NoArgsConstructor
public class AppSetting extends BaseAuditEntity {

    @NotBlank
    @Size(max = 120)
    @Column(name = "setting_key", nullable = false, length = 120)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 4000)
    private String settingValue;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean secret;
}
