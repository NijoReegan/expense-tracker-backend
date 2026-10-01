package com.expensetracker.expense_tracker_backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BudgetRequest(
        @NotBlank(message = "Budget name is required.")
        @Size(max = 60)
        String name,

        @Size(max = 60)
        String icon,

        @Size(max = 100)
        String iconBg,

        @DecimalMin(value = "0.00", message = "Spent amount cannot be negative.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal spent,

        @NotNull(message = "Budget total is required.")
        @DecimalMin(value = "0.01", message = "Budget total must be greater than zero.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal total) {
}