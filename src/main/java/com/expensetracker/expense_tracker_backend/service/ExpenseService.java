package com.expensetracker.expense_tracker_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker_backend.dto.request.ExpenseRequest;
import com.expensetracker.expense_tracker_backend.dto.response.ExpenseResponse;
import com.expensetracker.expense_tracker_backend.entity.Expense;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseResponse> list(User user) {
        return expenseRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    public ExpenseResponse get(User user, Long id) {
        return ExpenseResponse.from(requireOwned(user, id));
    }

    public ExpenseResponse create(User user, ExpenseRequest request) {
        Expense expense = new Expense();
        applyRequest(expense, request);
        expense.setUser(user);
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public ExpenseResponse update(User user, Long id, ExpenseRequest request) {
        Expense expense = requireOwned(user, id);
        applyRequest(expense, request);
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public void delete(User user, Long id) {
        expenseRepository.delete(requireOwned(user, id));
    }

    private Expense requireOwned(User user, Long id) {
        return expenseRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found."));
    }

    private void applyRequest(Expense expense, ExpenseRequest request) {
        expense.setName(request.name().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category().trim());
        expense.setDate(request.date());
        expense.setIcon(request.icon());
        expense.setIconBg(request.iconBg());
        expense.setCatCls(request.catCls());
    }
}