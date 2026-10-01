package com.expensetracker.expense_tracker_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.expensetracker.expense_tracker_backend.dto.request.BudgetRequest;
import com.expensetracker.expense_tracker_backend.entity.Budget;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.repository.AppNotificationRepository;
import com.expensetracker.expense_tracker_backend.repository.BudgetRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private AppNotificationRepository notificationRepository;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService(budgetRepository, notificationRepository);
    }

    @Test
    void overBudgetCreateGeneratesNotificationOnce() {
        User user = new User();
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));
        when(notificationRepository.existsByUserIdAndTitleAndMessage(any(), any(), any())).thenReturn(false);

        budgetService.create(user, new BudgetRequest("Food & Dining", "restaurant", "bg-x",
                new BigDecimal("150.00"), new BigDecimal("100.00")));

        verify(notificationRepository).save(any());
    }

    @Test
    void withinBudgetCreateDoesNotGenerateNotification() {
        User user = new User();
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        budgetService.create(user, new BudgetRequest("Rent & Utilities", "home", "bg-x",
                new BigDecimal("50.00"), new BigDecimal("100.00")));

        verify(notificationRepository, never()).save(any());
    }
}