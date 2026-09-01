package com.pamir.ppfarmsbackend.analytics.service;

import com.pamir.ppfarmsbackend.analytics.dto.DashboardAnalyticsResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {

    DashboardAnalyticsResponse getDashboardAnalytics(UUID organizationId);
    DashboardAnalyticsResponse getDashboardAnalytics(UUID organizationId, UUID speciesId);
    List<Map<String, Object>> getMilkProductionTrend(UUID organizationId, int days);
    List<Map<String, Object>> getMilkProductionTrend(UUID organizationId, int days, UUID speciesId);
    List<Map<String, Object>> getFinancialTrend(UUID organizationId, int months);
}

