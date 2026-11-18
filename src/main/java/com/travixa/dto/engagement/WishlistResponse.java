package com.travixa.dto.engagement;

import com.travixa.enums.WishlistType;

public record WishlistResponse(Long id, WishlistType itemType, Long targetId, String note) {
}
