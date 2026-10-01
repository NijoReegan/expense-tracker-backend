package com.expensetracker.expense_tracker_backend.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IncomeRequest(
        @NotBlank(message = "Income name is required.")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Income source is required.")
        @Size(max = 120)
        String source,

        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.00", message = "Amount cannot be negative.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @NotNull(message = "Date is required.")
        Instant date,

        Boolean recurring,

        @Size(max = 20)
        String frequency,

        @Size(max = 60)
        String icon,

        @Size(max = 100)
        String iconBg) {
}