package com.travixa.controllers;

import com.travixa.dto.support_platform.SettingResponse;
import com.travixa.entity.platform_cms_support.AppSetting;
import com.travixa.services.platform.SettingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public List<SettingResponse> findAll() {
        return settingService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SettingResponse findById(@PathVariable Long id) {
        return settingService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found"));
    }

    private SettingResponse toResponse(AppSetting setting) {
        return new SettingResponse(setting.getSettingKey(), setting.getDescription(), setting.isSecret());
    }
}
