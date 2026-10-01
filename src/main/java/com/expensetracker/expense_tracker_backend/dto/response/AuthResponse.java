package com.expensetracker.expense_tracker_backend.dto.response;

public record AuthResponse(String token, UserResponse user) {
}