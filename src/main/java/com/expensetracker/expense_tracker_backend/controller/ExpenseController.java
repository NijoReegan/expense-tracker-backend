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

import com.expensetracker.expense_tracker_backend.dto.request.ExpenseRequest;
import com.expensetracker.expense_tracker_backend.dto.response.ExpenseResponse;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CurrentUserResolver currentUserResolver;

    public ExpenseController(ExpenseService expenseService, CurrentUserResolver currentUserResolver) {
        this.expenseService = expenseService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> list() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(expenseService.list(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> get(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(expenseService.get(user, id));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ExpenseRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(expenseService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        expenseService.delete(user, id);
        return ResponseEntity.ok(new OkResponse(true));
    }
}