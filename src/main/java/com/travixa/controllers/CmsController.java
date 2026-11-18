package com.travixa.controllers;

import com.travixa.dto.support_platform.CmsPageRequest;
import com.travixa.dto.support_platform.CmsPageResponse;
import com.travixa.entity.platform_cms_support.CmsPage;
import com.travixa.entity.platform_cms_support.Language;
import com.travixa.mapper.CmsPageMapper;
import com.travixa.repository.platform.LanguageRepository;
import com.travixa.services.platform.CmsContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/cms-pages")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN', 'CONTENT_MANAGER')")
public class CmsController {

    private final CmsContentService cmsContentService;
    private final LanguageRepository languageRepository;

    public CmsController(CmsContentService cmsContentService, LanguageRepository languageRepository) {
        this.cmsContentService = cmsContentService;
        this.languageRepository = languageRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CmsPageResponse> findAll() {
        return cmsContentService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/languages")
    @Transactional(readOnly = true)
    public List<LanguageResponse> findEnabledLanguages() {
        return languageRepository.findAll().stream()
                .filter(Language::isEnabled)
                .map(language -> new LanguageResponse(
                        language.getId(), language.getCode(), language.getDisplayName(),
                        language.getNativeName(), language.isDefaultLanguage()))
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public CmsPageResponse findById(@PathVariable Long id) {
        return cmsContentService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CMS page not found"));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CmsPageResponse> create(@Valid @RequestBody CmsPageRequest request) {
        if (request.languageId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "languageId is required");
        }
        CmsPage page = new CmsPage();
        page.setSlug(request.slug());
        page.setTitle(request.title());
        page.setContent(request.content());
        page.setStatus(request.status());
        page.setLanguage(languageRepository.findById(request.languageId())
                .filter(Language::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enabled language not found")));
        if (request.status() == com.travixa.enums.ContentStatus.PUBLISHED) {
            page.setPublishedAt(Instant.now());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cmsContentService.save(page)));
    }

    @PutMapping("/{id}")
    @Transactional
    public CmsPageResponse update(@PathVariable Long id, @Valid @RequestBody CmsPageRequest request) {
        if (request.languageId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "languageId is required");
        }
        CmsPage page = cmsContentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CMS page not found"));
        page.setSlug(request.slug());
        page.setTitle(request.title());
        page.setContent(request.content());
        page.setStatus(request.status());
        page.setLanguage(languageRepository.findById(request.languageId())
                .filter(Language::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enabled language not found")));
        page.setPublishedAt(request.status() == com.travixa.enums.ContentStatus.PUBLISHED
                ? (page.getPublishedAt() == null ? Instant.now() : page.getPublishedAt())
                : null);
        return toResponse(cmsContentService.save(page));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (cmsContentService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CMS page not found");
        }
        cmsContentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CmsPageResponse toResponse(CmsPage page) {
        return CmsPageMapper.toResponse(page);
    }

    public record LanguageResponse(Long id, String code, String displayName, String nativeName, boolean defaultLanguage) { }
}
