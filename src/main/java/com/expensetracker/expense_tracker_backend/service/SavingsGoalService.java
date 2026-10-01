package com.expensetracker.expense_tracker_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker_backend.dto.request.SavingsGoalRequest;
import com.expensetracker.expense_tracker_backend.dto.response.SavingsGoalResponse;
import com.expensetracker.expense_tracker_backend.entity.SavingsGoal;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.SavingsGoalRepository;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
    }

    public List<SavingsGoalResponse> list(User user) {
        return savingsGoalRepository.findByUserIdOrderByIdAsc(user.getId())
                .stream()
                .map(SavingsGoalResponse::from)
                .toList();
    }

    public SavingsGoalResponse get(User user, Long id) {
        return SavingsGoalResponse.from(requireOwned(user, id));
    }

    public SavingsGoalResponse create(User user, SavingsGoalRequest request) {
        SavingsGoal goal = new SavingsGoal();
        applyRequest(goal, request);
        goal.setUser(user);
        return SavingsGoalResponse.from(savingsGoalRepository.save(goal));
    }

    public SavingsGoalResponse update(User user, Long id, SavingsGoalRequest request) {
        SavingsGoal goal = requireOwned(user, id);
        applyRequest(goal, request);
        return SavingsGoalResponse.from(savingsGoalRepository.save(goal));
    }

    public void delete(User user, Long id) {
        savingsGoalRepository.delete(requireOwned(user, id));
    }

    private SavingsGoal requireOwned(User user, Long id) {
        return savingsGoalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found."));
    }

    private void applyRequest(SavingsGoal goal, SavingsGoalRequest request) {
        goal.setName(request.name().trim());
        goal.setTargetDate(request.targetDate() == null || request.targetDate().isBlank()
                ? "TBD" : request.targetDate().trim());
        goal.setIcon(request.icon());
        goal.setSaved(request.saved() == null ? BigDecimal.ZERO : request.saved());
        goal.setTargetAmount(request.targetAmount());
    }
}