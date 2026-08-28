package com.pamir.ppfarmsbackend.crm.controller;

import com.pamir.ppfarmsbackend.crm.dto.SupplierRequest;
import com.pamir.ppfarmsbackend.crm.dto.SupplierResponse;
import com.pamir.ppfarmsbackend.crm.service.SupplierService;
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
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management (CRM)", description = "Endpoints for managing feed, medicine, animal, and equipment vendors")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Supplier", description = "Registers a new vendor/supplier profile")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@RequestBody @Valid SupplierRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        SupplierResponse response = supplierService.createSupplier(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Suppliers", description = "Retrieves all registered suppliers for the farm")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Supplier by ID", description = "Retrieves details of a specific supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(@PathVariable UUID id,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        SupplierResponse response = supplierService.getSupplierById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Supplier", description = "Updates details for an existing supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(@PathVariable UUID id,
                                                                         @RequestBody @Valid SupplierRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        SupplierResponse response = supplierService.updateSupplier(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Supplier", description = "Deactivates a supplier record")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable UUID id,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        supplierService.deleteSupplier(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully", null));
    }
}
