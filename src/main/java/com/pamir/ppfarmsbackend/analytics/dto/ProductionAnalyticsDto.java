package com.pamir.ppfarmsbackend.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionAnalyticsDto {

    private BigDecimal totalMilkThisMonth;
    private BigDecimal averageDailyMilkYield;
    private BigDecimal averageFatPercentage;
    private BigDecimal averageSnfPercentage;
    private Map<String, BigDecimal> productionByType;

    public BigDecimal getTotalMilkThisMonth() { return totalMilkThisMonth; } public void setTotalMilkThisMonth(BigDecimal totalMilkThisMonth) { this.totalMilkThisMonth = totalMilkThisMonth; }
    public BigDecimal getAverageDailyMilkYield() { return averageDailyMilkYield; } public void setAverageDailyMilkYield(BigDecimal averageDailyMilkYield) { this.averageDailyMilkYield = averageDailyMilkYield; }
    public BigDecimal getAverageFatPercentage() { return averageFatPercentage; } public void setAverageFatPercentage(BigDecimal averageFatPercentage) { this.averageFatPercentage = averageFatPercentage; }
    public BigDecimal getAverageSnfPercentage() { return averageSnfPercentage; } public void setAverageSnfPercentage(BigDecimal averageSnfPercentage) { this.averageSnfPercentage = averageSnfPercentage; }
    public Map<String, BigDecimal> getProductionByType() { return productionByType; } public void setProductionByType(Map<String, BigDecimal> productionByType) { this.productionByType = productionByType; }

    public static ProductionAnalyticsDtoBuilder builder() { return new ProductionAnalyticsDtoBuilder(); }
    public static class ProductionAnalyticsDtoBuilder {
        private final ProductionAnalyticsDto dto = new ProductionAnalyticsDto();
        public ProductionAnalyticsDtoBuilder totalMilkThisMonth(BigDecimal totalMilkThisMonth) { dto.setTotalMilkThisMonth(totalMilkThisMonth); return this; }
        public ProductionAnalyticsDtoBuilder averageDailyMilkYield(BigDecimal averageDailyMilkYield) { dto.setAverageDailyMilkYield(averageDailyMilkYield); return this; }
        public ProductionAnalyticsDtoBuilder averageFatPercentage(BigDecimal averageFatPercentage) { dto.setAverageFatPercentage(averageFatPercentage); return this; }
        public ProductionAnalyticsDtoBuilder averageSnfPercentage(BigDecimal averageSnfPercentage) { dto.setAverageSnfPercentage(averageSnfPercentage); return this; }
        public ProductionAnalyticsDtoBuilder productionByType(Map<String, BigDecimal> productionByType) { dto.setProductionByType(productionByType); return this; }
        public ProductionAnalyticsDto build() { return dto; }
    }
}
