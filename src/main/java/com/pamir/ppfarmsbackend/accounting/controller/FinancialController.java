package com.pamir.ppfarmsbackend.accounting.controller;

import com.pamir.ppfarmsbackend.accounting.dto.*;
import com.pamir.ppfarmsbackend.accounting.service.FinancialService;
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

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
@Tag(name = "Financial Accounting & Ledger", description = "Endpoints for logging farm income, operating expenses, and calculating financial P&L summary")
@SecurityRequirement(name = "Bearer Authentication")
public class FinancialController {

    private final FinancialService financialService;

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Log Farm Expense", description = "Records operating expense (Feed, Vet, Labor, Utilities, Maintenance)")
    public ResponseEntity<ApiResponse<ExpenseResponse>> logExpense(
            @Valid @RequestBody ExpenseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ExpenseResponse response = financialService.logExpense(userDetails.getTenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense logged successfully", response));
    }

    @GetMapping("/expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Farm Expenses", description = "Retrieves all expense records for current farm")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ExpenseResponse> response = financialService.getExpenses(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/incomes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Log Farm Income", description = "Records farm revenue (Livestock, Milk, Meat, Manure, Subsidies)")
    public ResponseEntity<ApiResponse<IncomeResponse>> logIncome(
            @Valid @RequestBody IncomeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        IncomeResponse response = financialService.logIncome(userDetails.getTenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Income logged successfully", response));
    }

    @GetMapping("/incomes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Farm Incomes", description = "Retrieves all income records for current farm")
    public ResponseEntity<ApiResponse<List<IncomeResponse>>> getIncomes(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<IncomeResponse> response = financialService.getIncomes(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Get Financial P&L Summary", description = "Calculates total revenue, total expenses, net profit/loss, and breakdown by category")
    public ResponseEntity<ApiResponse<FinancialSummaryResponse>> getFinancialSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        FinancialSummaryResponse response = financialService.getFinancialSummary(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
