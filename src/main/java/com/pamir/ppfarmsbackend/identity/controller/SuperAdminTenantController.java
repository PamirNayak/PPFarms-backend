package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.TenantSummaryDto;
import com.pamir.ppfarmsbackend.identity.service.SuperAdminTenantService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/super-admin/tenants")
@RequiredArgsConstructor
@Tag(name = "Super Admin Tenant & Farm Admin Management", description = "Endpoints for Super Admins to list, view, suspend, and manage all farm organizations")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminTenantController {

    private final SuperAdminTenantService tenantService;

    @GetMapping
    @Operation(summary = "Get All Farm Tenants", description = "List all registered farm organizations with search and status filter")
    public ResponseEntity<ApiResponse<List<TenantSummaryDto>>> getAllTenants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        List<TenantSummaryDto> list = tenantService.getAllTenants(search, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Tenant Dossier", description = "Get full profile, owner details, active plan, and usage stats for a farm")
    public ResponseEntity<ApiResponse<TenantSummaryDto>> getTenantDetails(@PathVariable UUID id) {
        TenantSummaryDto tenant = tenantService.getTenantDetails(id);
        return ResponseEntity.ok(ApiResponse.success(tenant));
    }

    @PostMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle Tenant Status", description = "Activates or suspends a farm tenant organization")
    public ResponseEntity<ApiResponse<TenantSummaryDto>> toggleStatus(@PathVariable UUID id) {
        TenantSummaryDto updated = tenantService.toggleTenantStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant status updated to " + updated.getStatus(), updated));
    }

    @PostMapping("/{id}/extend-trial")
    @Operation(summary = "Extend Free Trial / Subscription", description = "Manually adds days to a farm's subscription")
    public ResponseEntity<ApiResponse<TenantSummaryDto>> extendTrial(
            @PathVariable UUID id,
            @RequestBody ExtendTrialRequest request) {
        int days = request.getDays() > 0 ? request.getDays() : 14;
        TenantSummaryDto updated = tenantService.extendTrial(id, days);
        return ResponseEntity.ok(ApiResponse.success("Subscription extended by " + days + " days", updated));
    }

    @PostMapping("/{id}/assign-plan")
    @Operation(summary = "Assign SaaS Plan", description = "Manually assigns a plan to a tenant")
    public ResponseEntity<ApiResponse<TenantSummaryDto>> assignPlan(
            @PathVariable UUID id,
            @RequestBody AssignPlanRequest request) {
        TenantSummaryDto updated = tenantService.assignPlan(id, request.getPlanId(), request.getDurationDays());
        return ResponseEntity.ok(ApiResponse.success("Plan assigned successfully", updated));
    }

    @Data
    public static class ExtendTrialRequest {
        private int days;
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
    }

    @Data
    public static class AssignPlanRequest {
        private UUID planId;
        private int durationDays;
        public UUID getPlanId() { return planId; }
        public void setPlanId(UUID planId) { this.planId = planId; }
        public int getDurationDays() { return durationDays; }
        public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    }
}
