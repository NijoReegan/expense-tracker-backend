package com.expensetracker.expense_tracker_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker_backend.dto.request.BudgetRequest;
import com.expensetracker.expense_tracker_backend.dto.response.BudgetResponse;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.BudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CurrentUserResolver currentUserResolver;

    public BudgetController(BudgetService budgetService, CurrentUserResolver currentUserResolver) {
        this.budgetService = budgetService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> list() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(budgetService.list(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> get(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(budgetService.get(user, id));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody BudgetRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody BudgetRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(budgetService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        budgetService.delete(user, id);
        return ResponseEntity.ok(new OkResponse(true));
    }
}