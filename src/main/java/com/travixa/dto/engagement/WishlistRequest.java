package com.travixa.dto.engagement;

import com.travixa.enums.WishlistType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record WishlistRequest(
        @NotNull WishlistType itemType,
        @NotNull @Positive Long targetId,
        @Size(max = 1000) String note) {
}
