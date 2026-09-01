package com.pamir.ppfarmsbackend.analytics.controller;

import com.pamir.ppfarmsbackend.analytics.dto.DashboardAnalyticsResponse;
import com.pamir.ppfarmsbackend.analytics.service.AnalyticsService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Dashboard Analytics & Chart Time-Series", description = "Endpoints for executive dashboard KPIs and chart data visualization")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Dashboard Analytics KPIs", description = "Retrieves executive dashboard cards for Herd, Production, and Financial summary")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getDashboardAnalytics(
            @RequestParam(required = false) UUID speciesId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardAnalyticsResponse response = analyticsService.getDashboardAnalytics(userDetails.getTenantId(), speciesId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/milk-trend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Daily Milk Production Time-Series (Line Chart)", description = "Retrieves daily morning/evening milk yield trend for line chart visualization")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMilkProductionTrend(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) UUID speciesId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Map<String, Object>> trend = analyticsService.getMilkProductionTrend(userDetails.getTenantId(), days, speciesId);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    @GetMapping("/financial-trend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Monthly Financial P&L Time-Series (Stacked Bar Chart)", description = "Retrieves monthly income vs expense vs net profit trend for stacked bar chart")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFinancialTrend(
            @RequestParam(defaultValue = "6") int months,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Map<String, Object>> trend = analyticsService.getFinancialTrend(userDetails.getTenantId(), months);
        return ResponseEntity.ok(ApiResponse.success(trend));
    }
}
