package com.pamir.ppfarmsbackend.billing.controller;

import com.pamir.ppfarmsbackend.billing.dto.PaymentResponse;
import com.pamir.ppfarmsbackend.billing.dto.PaymentSubmitRequest;
import com.pamir.ppfarmsbackend.billing.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments & Subscription Billing", description = "Endpoints for submitting manual payment proof slips")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/submit-manual-proof")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Submit Manual Payment Proof", description = "Farm Admin submits transaction UTR number and Supabase receipt slip image URL")
    public ResponseEntity<ApiResponse<PaymentResponse>> submitManualProof(@RequestBody @Valid PaymentSubmitRequest request,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.submitPaymentProof(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment proof submitted successfully for verification", response));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Get Farm Payment History", description = "Retrieves past payment records for current farm")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PaymentResponse> history = paymentService.getFarmPaymentHistory(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/my-subscription")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Farm Subscription Details", description = "Retrieves current farm subscription status and days remaining")
    public ResponseEntity<ApiResponse<com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse>> getMySubscription(@AuthenticationPrincipal CustomUserDetails userDetails) {
        com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse response = paymentService.getFarmSubscription(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/claim-trial")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Claim 14-Day Free Trial", description = "Allows Farm Owner ADMIN to manually activate their 14-Day Free Trial")
    public ResponseEntity<ApiResponse<com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse>> claimTrial(@AuthenticationPrincipal CustomUserDetails userDetails) {
        com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse response = paymentService.claimFreeTrial(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("14-Day Free Trial activated successfully!", response));
    }
}
