package com.travixa.dto.engagement;

import com.travixa.enums.ReviewTargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(
        @NotNull ReviewTargetType targetType,
        @NotNull @Positive Long targetId,
        @Min(1) @Max(5) int rating,
        @Size(max = 150) String title,
        @Size(max = 5000) String comment) {
}
