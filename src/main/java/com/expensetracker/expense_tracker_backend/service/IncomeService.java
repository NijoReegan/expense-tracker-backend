package com.expensetracker.expense_tracker_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker_backend.dto.request.IncomeRequest;
import com.expensetracker.expense_tracker_backend.dto.response.IncomeResponse;
import com.expensetracker.expense_tracker_backend.entity.Income;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.IncomeRepository;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<IncomeResponse> list(User user) {
        return incomeRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(IncomeResponse::from)
                .toList();
    }

    public IncomeResponse get(User user, Long id) {
        return IncomeResponse.from(requireOwned(user, id));
    }

    public IncomeResponse create(User user, IncomeRequest request) {
        Income income = new Income();
        applyRequest(income, request);
        income.setUser(user);
        return IncomeResponse.from(incomeRepository.save(income));
    }

    public IncomeResponse update(User user, Long id, IncomeRequest request) {
        Income income = requireOwned(user, id);
        applyRequest(income, request);
        return IncomeResponse.from(incomeRepository.save(income));
    }

    public void delete(User user, Long id) {
        incomeRepository.delete(requireOwned(user, id));
    }

    private Income requireOwned(User user, Long id) {
        return incomeRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Income entry not found."));
    }

    private void applyRequest(Income income, IncomeRequest request) {
        income.setName(request.name().trim());
        income.setSource(request.source().trim());
        income.setAmount(request.amount());
        income.setDate(request.date());
        income.setRecurring(Boolean.TRUE.equals(request.recurring()));
        income.setFrequency(request.frequency());
        income.setIcon(request.icon());
        income.setIconBg(request.iconBg());
    }
}