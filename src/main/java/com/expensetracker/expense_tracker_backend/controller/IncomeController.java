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

import com.expensetracker.expense_tracker_backend.dto.request.IncomeRequest;
import com.expensetracker.expense_tracker_backend.dto.response.IncomeResponse;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;
    private final CurrentUserResolver currentUserResolver;

    public IncomeController(IncomeService incomeService, CurrentUserResolver currentUserResolver) {
        this.incomeService = incomeService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> list() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(incomeService.list(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> get(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(incomeService.get(user, id));
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> create(@Valid @RequestBody IncomeRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody IncomeRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(incomeService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OkResponse> delete(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        incomeService.delete(user, id);
        return ResponseEntity.ok(new OkResponse(true));
    }
}