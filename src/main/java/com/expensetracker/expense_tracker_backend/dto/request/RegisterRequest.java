package com.expensetracker.expense_tracker_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Full name is required.")
        @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters.")
        String fullName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.")
        @Size(max = 254)
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 6, max = 72, message = "Password must be at least 6 characters.")
        String password) {
}