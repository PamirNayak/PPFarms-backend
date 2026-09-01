package com.pamir.ppfarmsbackend.billing.controller;

import com.pamir.ppfarmsbackend.billing.dto.PaymentResponse;
import com.pamir.ppfarmsbackend.billing.service.PaymentService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/super-admin/payments")
@RequiredArgsConstructor
@Tag(name = "Super Admin Payment Governance", description = "Endpoints for Super Admins to review, approve, and reject manual payments")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get Pending Verification Queue", description = "List all payments submitted by Farm Admins awaiting verification")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingQueue() {
        List<PaymentResponse> pendingList = paymentService.getPendingPayments();
        return ResponseEntity.ok(ApiResponse.success(pendingList));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Approve Manual Payment", description = "Super Admin approves payment: activates subscription, extends end date, generates invoice")
    public ResponseEntity<ApiResponse<PaymentResponse>> approvePayment(@PathVariable UUID id,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.approvePayment(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment approved and subscription activated", response));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Reject Manual Payment", description = "Super Admin rejects payment with reason")
    public ResponseEntity<ApiResponse<PaymentResponse>> rejectPayment(@PathVariable UUID id,
                                                                       @RequestBody RejectReasonRequest request,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.rejectPayment(id, request.getReason(), userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment rejected", response));
    }

    @Data
    public static class RejectReasonRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
