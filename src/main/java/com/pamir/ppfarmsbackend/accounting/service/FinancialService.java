package com.pamir.ppfarmsbackend.accounting.service;

import com.pamir.ppfarmsbackend.accounting.dto.*;

import java.util.List;
import java.util.UUID;

public interface FinancialService {

    ExpenseResponse logExpense(UUID organizationId, ExpenseRequest request);

    List<ExpenseResponse> getExpenses(UUID organizationId);

    IncomeResponse logIncome(UUID organizationId, IncomeRequest request);

    List<IncomeResponse> getIncomes(UUID organizationId);

    FinancialSummaryResponse getFinancialSummary(UUID organizationId);
}
