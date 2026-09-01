package com.pamir.ppfarmsbackend.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAnalyticsDto {

    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfitLoss;
    private double profitMarginPercentage;
    private Map<String, BigDecimal> topExpenseCategories;
    private Map<String, BigDecimal> topIncomeCategories;

    public BigDecimal getTotalRevenue() { return totalRevenue; } public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalExpenses() { return totalExpenses; } public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    public BigDecimal getNetProfitLoss() { return netProfitLoss; } public void setNetProfitLoss(BigDecimal netProfitLoss) { this.netProfitLoss = netProfitLoss; }
    public double getProfitMarginPercentage() { return profitMarginPercentage; } public void setProfitMarginPercentage(double profitMarginPercentage) { this.profitMarginPercentage = profitMarginPercentage; }
    public Map<String, BigDecimal> getTopExpenseCategories() { return topExpenseCategories; } public void setTopExpenseCategories(Map<String, BigDecimal> topExpenseCategories) { this.topExpenseCategories = topExpenseCategories; }
    public Map<String, BigDecimal> getTopIncomeCategories() { return topIncomeCategories; } public void setTopIncomeCategories(Map<String, BigDecimal> topIncomeCategories) { this.topIncomeCategories = topIncomeCategories; }

    public static FinancialAnalyticsDtoBuilder builder() { return new FinancialAnalyticsDtoBuilder(); }
    public static class FinancialAnalyticsDtoBuilder {
        private final FinancialAnalyticsDto dto = new FinancialAnalyticsDto();
        public FinancialAnalyticsDtoBuilder totalRevenue(BigDecimal totalRevenue) { dto.setTotalRevenue(totalRevenue); return this; }
        public FinancialAnalyticsDtoBuilder totalExpenses(BigDecimal totalExpenses) { dto.setTotalExpenses(totalExpenses); return this; }
        public FinancialAnalyticsDtoBuilder netProfitLoss(BigDecimal netProfitLoss) { dto.setNetProfitLoss(netProfitLoss); return this; }
        public FinancialAnalyticsDtoBuilder profitMarginPercentage(double profitMarginPercentage) { dto.setProfitMarginPercentage(profitMarginPercentage); return this; }
        public FinancialAnalyticsDtoBuilder topExpenseCategories(Map<String, BigDecimal> topExpenseCategories) { dto.setTopExpenseCategories(topExpenseCategories); return this; }
        public FinancialAnalyticsDtoBuilder topIncomeCategories(Map<String, BigDecimal> topIncomeCategories) { dto.setTopIncomeCategories(topIncomeCategories); return this; }
        public FinancialAnalyticsDto build() { return dto; }
    }
}
