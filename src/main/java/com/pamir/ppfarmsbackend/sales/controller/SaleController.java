package com.pamir.ppfarmsbackend.sales.controller;

import com.pamir.ppfarmsbackend.sales.dto.SaleRequest;
import com.pamir.ppfarmsbackend.sales.dto.SaleResponse;
import com.pamir.ppfarmsbackend.sales.service.SaleService;
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
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Commercial Sales & Orders", description = "Endpoints for invoicing livestock sales, bulk milk supply, manure, and feed orders")
@SecurityRequirement(name = "Bearer Authentication")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Sales Invoice", description = "Records master-detail sales invoice with buyer info and item lines")
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(
            @Valid @RequestBody SaleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        SaleResponse response = saleService.createSale(userDetails.getTenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sales invoice created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Sales", description = "Retrieves all sales invoices for current farm")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SaleResponse> response = saleService.getSales(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{saleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Sale Invoice by ID", description = "Retrieves details and line items for a specific sale")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleById(
            @PathVariable UUID saleId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        SaleResponse response = saleService.getSaleById(userDetails.getTenantId(), saleId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
