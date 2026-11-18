package com.travixa.dto.booking;

import com.travixa.enums.BookingType;

public record CartItemResponse(
        Long id,
        Long cartId,
        BookingType resourceType,
        Long resourceId,
        int quantity,
        String optionsJson
) {
}