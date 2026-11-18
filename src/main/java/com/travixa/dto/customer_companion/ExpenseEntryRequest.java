package com.travixa.dto.customer_companion;

import com.travixa.enums.CurrencyCode;
import com.travixa.enums.ExpenseCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseEntryRequest(
        @NotNull ExpenseCategory category,
        @Size(max = 200) String description,
        @NotNull @Positive BigDecimal amount,
        @NotNull CurrencyCode currency,
        @NotNull LocalDate spentOn,
        @Size(max = 2000) String notes) {
}
