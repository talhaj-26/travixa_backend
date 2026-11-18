package com.travixa.controllers;

import com.travixa.dto.media.MediaUploadResponse;
import com.travixa.services.platform.MediaService;
import jakarta.validation.constraints.Size;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) { this.mediaService = mediaService; }

    @PostMapping(value = "/{type}/{listingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public List<MediaUploadResponse> upload(
            @PathVariable String type,
            @PathVariable Long listingId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(required = false) List<@Size(max = 160) String> altTexts,
            @RequestParam(required = false) Integer coverIndex) {
        return mediaService.upload(type, listingId, files, altTexts, coverIndex);
    }

    @GetMapping("/{type}/{listingId}/images")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public List<MediaUploadResponse> list(@PathVariable String type, @PathVariable Long listingId) {
        return mediaService.list(type, listingId);
    }

    @DeleteMapping("/{type}/images/{imageId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String type, @PathVariable Long imageId) {
        mediaService.delete(type, imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storageKey:.+}")
    public ResponseEntity<byte[]> read(@PathVariable String storageKey, Authentication authentication) {
        boolean administrator = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN")
                        || authority.getAuthority().equals("SUPER_ADMIN"));
        MediaService.StoredImage image = administrator
                ? mediaService.loadAnyListingImage(storageKey)
                : mediaService.loadPublished(storageKey);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .cacheControl(administrator
                        ? CacheControl.noCache().cachePrivate()
                        : CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .varyBy(HttpHeaders.AUTHORIZATION)
                .header("X-Content-Type-Options", "nosniff")
                .body(image.bytes());
    }
}
