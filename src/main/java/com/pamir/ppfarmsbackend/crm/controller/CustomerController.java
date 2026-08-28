package com.pamir.ppfarmsbackend.crm.controller;

import com.pamir.ppfarmsbackend.crm.dto.CustomerRequest;
import com.pamir.ppfarmsbackend.crm.dto.CustomerResponse;
import com.pamir.ppfarmsbackend.crm.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer / Buyer Management (CRM)", description = "Endpoints for managing livestock buyers, dairy cooperatives, and meat distributors")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Customer", description = "Registers a new customer / buyer profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@RequestBody @Valid CustomerRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        CustomerResponse response = customerService.createCustomer(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Customers", description = "Retrieves all registered buyers and customers for the farm")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CustomerResponse> customers = customerService.getAllCustomers(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Customer by ID", description = "Retrieves details of a specific customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        CustomerResponse response = customerService.getCustomerById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Customer", description = "Updates details for an existing customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(@PathVariable UUID id,
                                                                         @RequestBody @Valid CustomerRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        CustomerResponse response = customerService.updateCustomer(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Customer", description = "Deactivates a customer record")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable UUID id,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        customerService.deleteCustomer(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
}
