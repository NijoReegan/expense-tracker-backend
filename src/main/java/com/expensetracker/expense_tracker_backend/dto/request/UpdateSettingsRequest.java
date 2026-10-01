package com.expensetracker.expense_tracker_backend.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateSettingsRequest(
        @Size(min = 2, max = 3)
        String currency,

        @Size(min = 2, max = 2)
        String language,

        Boolean darkMode,
        Boolean glass) {
}