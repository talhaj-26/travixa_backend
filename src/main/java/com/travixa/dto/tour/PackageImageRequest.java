package com.travixa.dto.tour;
import jakarta.validation.constraints.*;
public record PackageImageRequest(@NotNull @Positive Long tourPackageId, @NotBlank @Size(max = 500) String storageKey, @Size(max = 160) String altText, @PositiveOrZero int displayOrder) {}
