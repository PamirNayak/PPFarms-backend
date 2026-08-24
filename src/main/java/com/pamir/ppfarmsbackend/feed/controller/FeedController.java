package com.pamir.ppfarmsbackend.feed.controller;

import com.pamir.ppfarmsbackend.feed.dto.*;
import com.pamir.ppfarmsbackend.feed.service.FeedService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Tag(name = "Feed Inventory & Ration Management", description = "Endpoints for managing feed stocks, low inventory alerts, and daily shed consumption logs")
@SecurityRequirement(name = "Bearer Authentication")
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/inventory")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Add/Update Feed Stock", description = "Creates or adds stock to feed inventory (Concentrates, Fodder, Silage, Mineral Mix)")
    public ResponseEntity<ApiResponse<FeedInventoryResponse>> addOrUpdateStock(@RequestBody @Valid FeedStockRequest request,
                                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        FeedInventoryResponse response = feedService.addOrUpdateFeedStock(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feed stock updated successfully", response));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Feed Inventory", description = "Retrieves all current feed items and stock levels for the farm")
    public ResponseEntity<ApiResponse<List<FeedInventoryResponse>>> getInventory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FeedInventoryResponse> inventory = feedService.getFeedInventory(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/inventory/low-stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Low Stock Alerts", description = "Lists feed items where current stock is at or below minimum threshold")
    public ResponseEntity<ApiResponse<List<FeedInventoryResponse>>> getLowStockAlerts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FeedInventoryResponse> alerts = feedService.getLowStockAlerts(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    @PostMapping("/consumption")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Feed Consumption", description = "Records daily feed consumed by a shed/pen and automatically deducts quantity from inventory stock")
    public ResponseEntity<ApiResponse<FeedLogResponse>> logConsumption(@RequestBody @Valid FeedConsumptionRequest request,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        FeedLogResponse response = feedService.logConsumption(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feed consumption logged and stock deducted", response));
    }

    @GetMapping("/consumption")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Consumption Logs", description = "Retrieves feed consumption history within date range")
    public ResponseEntity<ApiResponse<List<FeedLogResponse>>> getConsumptionLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FeedLogResponse> logs = feedService.getConsumptionLogs(userDetails.getTenantId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
