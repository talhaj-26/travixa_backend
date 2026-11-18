package com.travixa.dto.tour;
public record PackageImageResponse(Long id, Long tourPackageId, String storageKey, String publicUrl, String altText, int displayOrder, boolean coverImage) {}
