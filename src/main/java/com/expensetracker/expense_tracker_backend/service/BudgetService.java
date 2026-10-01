package com.expensetracker.expense_tracker_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expense_tracker_backend.dto.request.BudgetRequest;
import com.expensetracker.expense_tracker_backend.dto.response.BudgetResponse;
import com.expensetracker.expense_tracker_backend.entity.AppNotification;
import com.expensetracker.expense_tracker_backend.entity.Budget;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.AppNotificationRepository;
import com.expensetracker.expense_tracker_backend.repository.BudgetRepository;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AppNotificationRepository notificationRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         AppNotificationRepository notificationRepository) {
        this.budgetRepository = budgetRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<BudgetResponse> list(User user) {
        return budgetRepository.findByUserIdOrderByIdAsc(user.getId())
                .stream()
                .map(BudgetResponse::from)
                .toList();
    }

    public BudgetResponse get(User user, Long id) {
        return BudgetResponse.from(requireOwned(user, id));
    }

    @Transactional
    public BudgetResponse create(User user, BudgetRequest request) {
        Budget budget = new Budget();
        applyRequest(budget, request);
        budget.setUser(user);
        Budget saved = budgetRepository.save(budget);
        reconcileBudgetAlert(user, saved);
        return BudgetResponse.from(saved);
    }

    @Transactional
    public BudgetResponse update(User user, Long id, BudgetRequest request) {
        Budget budget = requireOwned(user, id);
        applyRequest(budget, request);
        Budget saved = budgetRepository.save(budget);
        reconcileBudgetAlert(user, saved);
        return BudgetResponse.from(saved);
    }

    public void delete(User user, Long id) {
        budgetRepository.delete(requireOwned(user, id));
    }

    private Budget requireOwned(User user, Long id) {
        return budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found."));
    }

    private void applyRequest(Budget budget, BudgetRequest request) {
        budget.setName(request.name().trim());
        budget.setIcon(request.icon());
        budget.setIconBg(request.iconBg());
        budget.setSpent(request.spent() == null ? BigDecimal.ZERO : request.spent());
        budget.setTotal(request.total());
    }

    /**
     * Mirrors the frontend's mock behaviour: when a budget is exceeded, surface a
     * "Budget Exceeded" notification once per budget.
     */
    private void reconcileBudgetAlert(User user, Budget budget) {
        if (budget.getSpent().compareTo(budget.getTotal()) <= 0) {
            return;
        }
        String message = "You're over your " + budget.getName() + " budget for this month.";
        if (notificationRepository.existsByUserIdAndTitleAndMessage(
                user.getId(), "Budget Exceeded", message)) {
            return;
        }
        AppNotification notification = new AppNotification();
        notification.setUser(user);
        notification.setTitle("Budget Exceeded");
        notification.setMessage(message);
        notification.setIcon("warning");
        notification.setIconBg("bg-error/10 text-error");
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}