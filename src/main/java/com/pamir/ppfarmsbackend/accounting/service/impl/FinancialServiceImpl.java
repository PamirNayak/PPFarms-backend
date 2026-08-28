package com.pamir.ppfarmsbackend.accounting.service.impl;

import com.pamir.ppfarmsbackend.accounting.dto.*;
import com.pamir.ppfarmsbackend.accounting.entity.Expense;
import com.pamir.ppfarmsbackend.accounting.entity.Income;
import com.pamir.ppfarmsbackend.accounting.repository.ExpenseRepository;
import com.pamir.ppfarmsbackend.accounting.repository.IncomeRepository;
import com.pamir.ppfarmsbackend.accounting.service.FinancialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FinancialServiceImpl implements FinancialService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public FinancialServiceImpl(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @Override
    public ExpenseResponse logExpense(UUID organizationId, ExpenseRequest request) {
        Expense expense = Expense.builder()
                .organizationId(organizationId)
                .category(request.getCategory())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .vendorName(request.getVendorName())
                .receiptNumber(request.getReceiptNumber())
                .paymentMethod(request.getPaymentMethod())
                .build();

        Expense saved = expenseRepository.save(expense);
        return mapExpense(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(UUID organizationId) {
        return expenseRepository.findByOrganizationIdOrderByExpenseDateDesc(organizationId)
                .stream()
                .map(this::mapExpense)
                .toList();
    }

    @Override
    public IncomeResponse logIncome(UUID organizationId, IncomeRequest request) {
        Income income = Income.builder()
                .organizationId(organizationId)
                .category(request.getCategory())
                .description(request.getDescription())
                .amount(request.getAmount())
                .incomeDate(request.getIncomeDate())
                .sourceName(request.getSourceName())
                .referenceNumber(request.getReferenceNumber())
                .build();

        Income saved = incomeRepository.save(income);
        return mapIncome(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomes(UUID organizationId) {
        return incomeRepository.findByOrganizationIdOrderByIncomeDateDesc(organizationId)
                .stream()
                .map(this::mapIncome)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialSummaryResponse getFinancialSummary(UUID organizationId) {
        List<Income> incomes = incomeRepository.findByOrganizationIdOrderByIncomeDateDesc(organizationId);
        List<Expense> expenses = expenseRepository.findByOrganizationIdOrderByExpenseDateDesc(organizationId);

        BigDecimal totalIncome = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeCatMap = new HashMap<>();
        for (Income inc : incomes) {
            totalIncome = totalIncome.add(inc.getAmount());
            String catName = inc.getCategory() != null ? inc.getCategory().name() : "OTHER";
            incomeCatMap.put(catName, incomeCatMap.getOrDefault(catName, BigDecimal.ZERO).add(inc.getAmount()));
        }

        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> expenseCatMap = new HashMap<>();
        for (Expense exp : expenses) {
            totalExpense = totalExpense.add(exp.getAmount());
            String catName = exp.getCategory() != null ? exp.getCategory().name() : "OTHER";
            expenseCatMap.put(catName, expenseCatMap.getOrDefault(catName, BigDecimal.ZERO).add(exp.getAmount()));
        }

        BigDecimal netProfitLoss = totalIncome.subtract(totalExpense);

        return FinancialSummaryResponse.builder()
                .organizationId(organizationId)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netProfitLoss(netProfitLoss)
                .incomeByCategory(incomeCatMap)
                .expenseByCategory(expenseCatMap)
                .build();
    }

    private ExpenseResponse mapExpense(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .organizationId(e.getOrganizationId())
                .category(e.getCategory())
                .description(e.getDescription())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .vendorName(e.getVendorName())
                .receiptNumber(e.getReceiptNumber())
                .paymentMethod(e.getPaymentMethod())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private IncomeResponse mapIncome(Income i) {
        return IncomeResponse.builder()
                .id(i.getId())
                .organizationId(i.getOrganizationId())
                .category(i.getCategory())
                .description(i.getDescription())
                .amount(i.getAmount())
                .incomeDate(i.getIncomeDate())
                .sourceName(i.getSourceName())
                .referenceNumber(i.getReferenceNumber())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
