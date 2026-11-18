package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
}
