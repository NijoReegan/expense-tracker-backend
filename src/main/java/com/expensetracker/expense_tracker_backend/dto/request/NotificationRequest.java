package com.expensetracker.expense_tracker_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotBlank(message = "Notification title is required.")
        @Size(max = 120)
        String title,

        @NotBlank(message = "Notification message is required.")
        String message,

        @Size(max = 60)
        String icon,

        @Size(max = 100)
        String iconBg,

        Boolean read) {
}