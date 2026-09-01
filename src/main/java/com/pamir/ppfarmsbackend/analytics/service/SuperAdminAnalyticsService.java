package com.pamir.ppfarmsbackend.analytics.service;

import com.pamir.ppfarmsbackend.analytics.dto.SuperAdminAnalyticsDto;

import java.util.List;
import java.util.Map;

public interface SuperAdminAnalyticsService {
    SuperAdminAnalyticsDto getPlatformOverview();
    List<Map<String, Object>> getMonthlyRevenueTrend();
}
