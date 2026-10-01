package com.expensetracker.expense_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expense_tracker_backend.entity.SavingsGoal;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUserIdOrderByIdAsc(Long userId);

    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
}