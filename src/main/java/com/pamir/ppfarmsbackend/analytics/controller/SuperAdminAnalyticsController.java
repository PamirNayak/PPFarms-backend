package com.pamir.ppfarmsbackend.analytics.controller;

import com.pamir.ppfarmsbackend.analytics.dto.SuperAdminAnalyticsDto;
import com.pamir.ppfarmsbackend.analytics.service.SuperAdminAnalyticsService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/super-admin/analytics")
@RequiredArgsConstructor
@Tag(name = "Super Admin Platform Analytics", description = "Endpoints for platform turnover, MRR, ARR, and tenant growth metrics")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminAnalyticsController {

    private final SuperAdminAnalyticsService superAdminAnalyticsService;

    @GetMapping("/overview")
    @Operation(summary = "Get Platform SaaS Overview", description = "Retrieves gross turnover, MRR, ARR, active paid tenants, and platform livestock totals")
    public ResponseEntity<ApiResponse<SuperAdminAnalyticsDto>> getPlatformOverview() {
        SuperAdminAnalyticsDto data = superAdminAnalyticsService.getPlatformOverview();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/revenue-trend")
    @Operation(summary = "Get Platform Revenue Trend", description = "Retrieves monthly revenue breakdown for platform growth charts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRevenueTrend() {
        List<Map<String, Object>> data = superAdminAnalyticsService.getMonthlyRevenueTrend();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
