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

import com.expensetracker.expense_tracker_backend.dto.request.SavingsGoalRequest;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.dto.response.SavingsGoalResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.SavingsGoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;
    private final CurrentUserResolver currentUserResolver;

    public SavingsGoalController(SavingsGoalService savingsGoalService,
                                 CurrentUserResolver currentUserResolver) {
        this.savingsGoalService = savingsGoalService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> list() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(savingsGoalService.list(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> get(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(savingsGoalService.get(user, id));
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> create(@Valid @RequestBody SavingsGoalRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsGoalService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody SavingsGoalRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(savingsGoalService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        savingsGoalService.delete(user, id);
        return ResponseEntity.ok(new OkResponse(true));
    }
}