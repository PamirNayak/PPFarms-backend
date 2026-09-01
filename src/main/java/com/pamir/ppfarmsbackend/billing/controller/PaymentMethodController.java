package com.pamir.ppfarmsbackend.billing.controller;

import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountRequest;
import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountResponse;
import com.pamir.ppfarmsbackend.billing.service.SystemBankAccountService;
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

@RestController
@RequestMapping("/api/v1/payment-methods")
@RequiredArgsConstructor
@Tag(name = "Super Admin Bank Accounts & QR Codes", description = "Endpoints for viewing active Super Admin bank details & QR codes for payment transfers")
public class PaymentMethodController {

    private final SystemBankAccountService bankAccountService;

    @GetMapping
    @Operation(summary = "Get Active Super Admin Payment Methods", description = "Lists active Super Admin bank accounts and UPI QR code details for manual payment proof submission")
    public ResponseEntity<ApiResponse<List<SystemBankAccountResponse>>> getActiveBankAccounts() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getActiveBankAccounts()));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Add Super Admin Bank Account / QR Code", description = "Super Admin adds new bank account or UPI QR code for receiving payments")
    public ResponseEntity<ApiResponse<SystemBankAccountResponse>> addBankAccount(@RequestBody @Valid SystemBankAccountRequest request) {
        SystemBankAccountResponse saved = bankAccountService.addBankAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank account added successfully", saved));
    }
}