package com.expensetracker.expense_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expense_tracker_backend.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    Optional<Income> findByIdAndUserId(Long id, Long userId);
}