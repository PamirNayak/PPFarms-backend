package com.pamir.ppfarmsbackend.accounting.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {

    private UUID organizationId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfitLoss;
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expenseByCategory;

    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public BigDecimal getTotalIncome() { return totalIncome; } public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; } public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
    public BigDecimal getNetProfitLoss() { return netProfitLoss; } public void setNetProfitLoss(BigDecimal netProfitLoss) { this.netProfitLoss = netProfitLoss; }
    public Map<String, BigDecimal> getIncomeByCategory() { return incomeByCategory; } public void setIncomeByCategory(Map<String, BigDecimal> incomeByCategory) { this.incomeByCategory = incomeByCategory; }
    public Map<String, BigDecimal> getExpenseByCategory() { return expenseByCategory; } public void setExpenseByCategory(Map<String, BigDecimal> expenseByCategory) { this.expenseByCategory = expenseByCategory; }

    public static FinancialSummaryResponseBuilder builder() { return new FinancialSummaryResponseBuilder(); }
    public static class FinancialSummaryResponseBuilder {
        private final FinancialSummaryResponse r = new FinancialSummaryResponse();
        public FinancialSummaryResponseBuilder organizationId(UUID organizationId) { r.setOrganizationId(organizationId); return this; }
        public FinancialSummaryResponseBuilder totalIncome(BigDecimal totalIncome) { r.setTotalIncome(totalIncome); return this; }
        public FinancialSummaryResponseBuilder totalExpense(BigDecimal totalExpense) { r.setTotalExpense(totalExpense); return this; }
        public FinancialSummaryResponseBuilder netProfitLoss(BigDecimal netProfitLoss) { r.setNetProfitLoss(netProfitLoss); return this; }
        public FinancialSummaryResponseBuilder incomeByCategory(Map<String, BigDecimal> incomeByCategory) { r.setIncomeByCategory(incomeByCategory); return this; }
        public FinancialSummaryResponseBuilder expenseByCategory(Map<String, BigDecimal> expenseByCategory) { r.setExpenseByCategory(expenseByCategory); return this; }
        public FinancialSummaryResponse build() { return r; }
    }
}
