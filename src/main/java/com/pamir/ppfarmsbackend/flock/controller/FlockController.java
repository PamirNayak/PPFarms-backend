package com.pamir.ppfarmsbackend.flock.controller;

import com.pamir.ppfarmsbackend.flock.dto.*;
import com.pamir.ppfarmsbackend.flock.entity.EggProductionLog;
import com.pamir.ppfarmsbackend.flock.entity.FlockMortalityLog;
import com.pamir.ppfarmsbackend.flock.service.FlockService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flocks")
@RequiredArgsConstructor
@Tag(name = "Flock & Poultry Batch Management", description = "Endpoints for poultry/small animal batch tracking, mortality logs, and egg production")
@SecurityRequirement(name = "Bearer Authentication")
public class FlockController {

    private final FlockService flockService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Flock Batch", description = "Registers a new flock/batch for poultry or small animals")
    public ResponseEntity<ApiResponse<FlockBatchResponse>> createBatch(
            @RequestBody @Valid FlockBatchRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FlockBatchResponse response = flockService.createBatch(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flock batch registered successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get All Flock Batches", description = "Retrieves all active and completed flock batches for the tenant")
    public ResponseEntity<ApiResponse<List<FlockBatchResponse>>> getBatches(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FlockBatchResponse> response = flockService.getBatches(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Flock Batch Profile", description = "Retrieves profile and metrics for a specific flock batch")
    public ResponseEntity<ApiResponse<FlockBatchResponse>> getBatchById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FlockBatchResponse response = flockService.getBatchById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Flock Batch", description = "Updates details of an existing flock batch")
    public ResponseEntity<ApiResponse<FlockBatchResponse>> updateBatch(
            @PathVariable UUID id,
            @RequestBody @Valid FlockBatchRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FlockBatchResponse response = flockService.updateBatch(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Flock batch updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete Flock Batch", description = "Soft deletes a flock batch")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        flockService.deleteBatch(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Flock batch deleted successfully", null));
    }

    @PostMapping("/mortality")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Flock Mortality", description = "Records dead bird count and auto-decrements active flock count")
    public ResponseEntity<ApiResponse<FlockMortalityLog>> logMortality(
            @RequestBody @Valid MortalityLogRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FlockMortalityLog log = flockService.logMortality(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mortality log recorded successfully", log));
    }

    @GetMapping("/mortality")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Mortality Logs", description = "Retrieves mortality records for a batch or whole farm")
    public ResponseEntity<ApiResponse<List<FlockMortalityLog>>> getMortalityLogs(
            @RequestParam(required = false) UUID flockBatchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FlockMortalityLog> logs = flockService.getMortalityLogs(flockBatchId, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @PostMapping("/eggs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'WORKER')")
    @Operation(summary = "Log Egg Collection", description = "Records daily egg yield for a layer batch")
    public ResponseEntity<ApiResponse<EggProductionLog>> logEggProduction(
            @RequestBody @Valid EggLogRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        EggProductionLog log = flockService.logEggProduction(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Egg collection logged successfully", log));
    }

    @GetMapping("/eggs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'WORKER')")
    @Operation(summary = "Get Egg Collection History", description = "Retrieves egg collection history")
    public ResponseEntity<ApiResponse<List<EggProductionLog>>> getEggLogs(
            @RequestParam(required = false) UUID flockBatchId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<EggProductionLog> logs = flockService.getEggLogs(flockBatchId, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
