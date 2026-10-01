package com.expensetracker.expense_tracker_backend.dto.response;

import java.time.Instant;

import com.expensetracker.expense_tracker_backend.entity.User;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Instant createdAt,
        String currency,
        String language,
        boolean darkMode,
        boolean glass) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getCurrency(),
                user.getLanguage(),
                user.isDarkMode(),
                user.isGlass());
    }
}