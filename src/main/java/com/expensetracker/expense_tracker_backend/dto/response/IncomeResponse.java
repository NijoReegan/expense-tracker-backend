package com.expensetracker.expense_tracker_backend.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.expensetracker.expense_tracker_backend.entity.Income;

public record IncomeResponse(
        Long id,
        String name,
        String source,
        BigDecimal amount,
        Instant date,
        boolean recurring,
        String frequency,
        String icon,
        String iconBg) {

    public static IncomeResponse from(Income i) {
        return new IncomeResponse(
                i.getId(),
                i.getName(),
                i.getSource(),
                i.getAmount(),
                i.getDate(),
                i.isRecurring(),
                i.getFrequency(),
                i.getIcon(),
                i.getIconBg());
    }
}