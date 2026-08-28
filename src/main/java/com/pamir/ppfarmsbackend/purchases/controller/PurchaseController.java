package com.pamir.ppfarmsbackend.purchases.controller;

import com.pamir.ppfarmsbackend.purchases.dto.PurchaseRequest;
import com.pamir.ppfarmsbackend.purchases.dto.PurchaseResponse;
import com.pamir.ppfarmsbackend.purchases.service.PurchaseService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Tag(name = "Procurement & Purchases", description = "Endpoints for logging master-detail purchases of feed, animals, equipment, and medicine")
@SecurityRequirement(name = "Bearer Authentication")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Record Master-Detail Purchase", description = "Logs vendor purchase order with line items (feed, animals, equipment)")
    public ResponseEntity<ApiResponse<PurchaseResponse>> createPurchase(@RequestBody @Valid PurchaseRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        PurchaseResponse response = purchaseService.createPurchase(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase recorded successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Purchase Order Details", description = "Retrieves purchase order details by ID")
    public ResponseEntity<ApiResponse<PurchaseResponse>> getPurchaseById(@PathVariable UUID id,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        PurchaseResponse response = purchaseService.getPurchaseById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List Farm Purchases", description = "Retrieves all farm purchase records within optional date range")
    public ResponseEntity<ApiResponse<List<PurchaseResponse>>> getPurchases(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PurchaseResponse> purchases = purchaseService.getPurchases(userDetails.getTenantId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(purchases));
    }
}
