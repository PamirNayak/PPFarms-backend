package com.pamir.ppfarmsbackend.billing.controller;

import com.pamir.ppfarmsbackend.billing.dto.PlanRequest;
import com.pamir.ppfarmsbackend.billing.dto.PlanResponse;
import com.pamir.ppfarmsbackend.billing.service.PlanService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Public Subscription Plans", description = "Endpoints for viewing public SaaS subscription pricing tiers")
public class PlanController {

    private final PlanService planService;

    @GetMapping
    @Operation(summary = "Get Public Subscription Tiers", description = "Lists all active subscription plan tiers for pricing table")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getActivePlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getActivePlans()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get All Subscription Plans", description = "Allows Super Admin to list all active and inactive subscription plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getAllPlans()));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create Subscription Plan Package", description = "Allows Super Admin to post a new subscription plan package")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(@RequestBody @Valid PlanRequest plan) {
        PlanResponse saved = planService.createPlan(plan);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription package created successfully", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update Subscription Plan Package", description = "Allows Super Admin to update an existing subscription plan package")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(@PathVariable UUID id, @RequestBody @Valid PlanRequest request) {
        PlanResponse updated = planService.updatePlan(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Deactivate Subscription Plan Package", description = "Allows Super Admin to deactivate a subscription plan package")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable UUID id) {
        planService.deactivatePlan(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deactivated successfully", null));
    }
}