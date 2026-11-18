package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.AppSetting;
import com.travixa.repository.platform.AppSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SettingService {

    private final AppSettingRepository appSettingRepository;

    public SettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    public List<AppSetting> findAll() {
        return appSettingRepository.findAll();
    }

    public Optional<AppSetting> findById(Long id) {
        return appSettingRepository.findById(id);
    }

    @Transactional
    public AppSetting save(AppSetting appSetting) {
        return appSettingRepository.save(appSetting);
    }
}
