package com.expensetracker.expense_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expense_tracker_backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);
}