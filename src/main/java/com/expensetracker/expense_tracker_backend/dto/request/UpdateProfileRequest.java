package com.expensetracker.expense_tracker_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters.")
        String fullName,

        @Email(message = "Enter a valid email address.")
        @Size(max = 254)
        String email) {
}