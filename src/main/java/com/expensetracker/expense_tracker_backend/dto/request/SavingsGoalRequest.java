package com.expensetracker.expense_tracker_backend.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SavingsGoalRequest(
        @NotBlank(message = "Goal name is required.")
        @Size(max = 120)
        String name,

        @NotBlank(message = "Target date is required.")
        @Size(max = 60)
        String targetDate,

        @Size(max = 60)
        String icon,

        @DecimalMin(value = "0.00", message = "Saved amount cannot be negative.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal saved,

        @NotNull(message = "Target amount is required.")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal targetAmount) {
}