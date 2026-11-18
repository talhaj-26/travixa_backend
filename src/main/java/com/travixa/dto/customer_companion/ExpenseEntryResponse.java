package com.travixa.dto.customer_companion;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseEntryResponse(
        Long id,
        ExpenseCategory category,
        String description,
        BigDecimal amount,
        CurrencyCode currency,
        LocalDate spentOn,
        String notes) {
}
