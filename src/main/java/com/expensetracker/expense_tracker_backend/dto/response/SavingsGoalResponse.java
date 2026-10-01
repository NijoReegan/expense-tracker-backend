package com.expensetracker.expense_tracker_backend.dto.response;

import java.math.BigDecimal;

import com.expensetracker.expense_tracker_backend.entity.SavingsGoal;

public record SavingsGoalResponse(
        Long id,
        String name,
        String targetDate,
        String icon,
        BigDecimal saved,
        BigDecimal targetAmount) {

    public static SavingsGoalResponse from(SavingsGoal g) {
        return new SavingsGoalResponse(
                g.getId(),
                g.getName(),
                g.getTargetDate(),
                g.getIcon(),
                g.getSaved(),
                g.getTargetAmount());
    }
}