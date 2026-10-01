package com.expensetracker.expense_tracker_backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.expensetracker.expense_tracker_backend.entity.Expense;

public record ExpenseResponse(
        Long id,
        String name,
        BigDecimal amount,
        String category,
        Instant date,
        String icon,
        String iconBg,
        String catCls) {

    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.getId(),
                e.getName(),
                e.getAmount(),
                e.getCategory(),
                e.getDate(),
                e.getIcon(),
                e.getIconBg(),
                e.getCatCls());
    }
}