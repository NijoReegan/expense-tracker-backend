package com.expensetracker.expense_tracker_backend.dto.response;

import java.time.Instant;

import com.expensetracker.expense_tracker_backend.entity.AppNotification;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        Instant createdAt,
        String icon,
        String iconBg,
        boolean read) {

    public static NotificationResponse from(AppNotification n) {
        return new NotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.getCreatedAt(),
                n.getIcon(),
                n.getIconBg(),
                n.isRead());
    }
}