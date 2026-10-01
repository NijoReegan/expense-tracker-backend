package com.expensetracker.expense_tracker_backend.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExpenseRequest(
        @NotBlank(message = "Merchant name is required.")
        @Size(max = 120)
        String name,

        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.00", message = "Amount cannot be negative.")
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @NotBlank(message = "Category is required.")
        @Size(max = 60)
        String category,

        @NotNull(message = "Date is required.")
        Instant date,

        @Size(max = 60)
        String icon,

        @Size(max = 100)
        String iconBg,

        @Size(max = 100)
        String catCls) {
}