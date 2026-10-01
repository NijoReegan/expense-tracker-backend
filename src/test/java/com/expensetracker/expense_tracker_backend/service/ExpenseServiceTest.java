package com.expensetracker.expense_tracker_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expensetracker.expense_tracker_backend.dto.request.ExpenseRequest;
import com.expensetracker.expense_tracker_backend.entity.Expense;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user(long id) {
        User u = new User();
        u.setEmail("u" + id + "@example.com");
        return u;
    }

    @Test
    void createAssociatesExpenseWithAuthenticatedUser() {
        when(expenseRepository.save(org.mockito.ArgumentMatchers.any(Expense.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = expenseService.create(user(7), new ExpenseRequest(
                "Coffee", new BigDecimal("3.50"), "Food & Dining", Instant.parse("2026-09-25T00:00:00Z"),
                "restaurant", "bg-x", "text-x"));

        assertEquals("Coffee", response.name());
        assertEquals("Food & Dining", response.category());
    }

    @Test
    void getMissingExpenseThrowsNotFound() {
        when(expenseRepository.findByIdAndUserId(eq(99L), any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> expenseService.get(user(7), 99L));
    }

    @Test
    void cannotReadAnotherUsersExpense() {
        User owner = user(8);
        Expense expense = new Expense();
        expense.setName("Private");
        when(expenseRepository.findByIdAndUserId(5L, owner.getId())).thenReturn(Optional.of(expense));

        var result = expenseService.get(owner, 5L);
        assertEquals("Private", result.name());
        verify(expenseRepository).findByIdAndUserId(5L, owner.getId());
    }
}