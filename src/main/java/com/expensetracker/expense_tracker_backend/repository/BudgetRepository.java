package com.expensetracker.expense_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expense_tracker_backend.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdOrderByIdAsc(Long userId);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}