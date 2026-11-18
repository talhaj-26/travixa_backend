package com.travixa.dto.engagement;

import com.travixa.enums.ReviewStatus;
import com.travixa.enums.ReviewTargetType;

public record ReviewResponse(
        Long id,
        ReviewTargetType targetType,
        Long targetId,
        int rating,
        String title,
        String comment,
        ReviewStatus status) {
}
