package com.expensetracker.expense_tracker_backend.dto.response;

import java.math.BigDecimal;

import com.expensetracker.expense_tracker_backend.entity.Budget;

public record BudgetResponse(
        Long id,
        String name,
        String icon,
        String iconBg,
        BigDecimal spent,
        BigDecimal total) {

    public static BudgetResponse from(Budget b) {
        return new BudgetResponse(
                b.getId(),
                b.getName(),
                b.getIcon(),
                b.getIconBg(),
                b.getSpent(),
                b.getTotal());
    }
}