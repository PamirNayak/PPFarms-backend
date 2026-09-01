package com.pamir.ppfarmsbackend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdminAnalyticsDto {
    private BigDecimal totalTurnover;
    private BigDecimal monthlyRecurringRevenue;
    private BigDecimal annualRecurringRevenue;
    
    private long totalTenants;
    private long activePaidTenants;
    private long trialTenants;
    private long expiredTenants;
    private long suspendedTenants;

    private long totalAnimalsAcrossPlatform;
    private long totalPendingPayments;
    private long totalInquiries;
    private double revenueGrowthPercent;

    private List<Map<String, Object>> monthlyRevenueTrend;
    private List<Map<String, Object>> recentTenants;
}
