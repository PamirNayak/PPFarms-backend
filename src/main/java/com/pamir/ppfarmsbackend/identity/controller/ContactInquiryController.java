package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryRequest;
import com.pamir.ppfarmsbackend.identity.dto.ContactInquiryResponse;
import com.pamir.ppfarmsbackend.identity.service.ContactInquiryService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.ratelimit.RateLimit;
import com.pamir.ppfarmsbackend.shared.security.ratelimit.RateLimitKeyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Landing Page Contact & Lead Inquiries", description = "Endpoints for public contact sales lead submission and Super Admin lead management")
public class ContactInquiryController {

    private final ContactInquiryService contactInquiryService;

    @PostMapping("/public/contact")
    @RateLimit(limit = 5, windowSeconds = 60, keyType = RateLimitKeyType.IP)
    @Operation(summary = "Submit Landing Page Contact Inquiry", description = "Public endpoint for SaaS landing page visitors to request demo or enterprise quote")
    public ResponseEntity<ApiResponse<ContactInquiryResponse>> submitInquiry(@RequestBody @Valid ContactInquiryRequest inquiry) {
        ContactInquiryResponse saved = contactInquiryService.submitInquiry(inquiry);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thank you for your interest! Our team will contact you shortly.", saved));
    }

    @GetMapping("/super-admin/inquiries")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get Sales Lead Inquiries", description = "Super Admin reviews inbound sales demo/quote requests")
    public ResponseEntity<ApiResponse<List<ContactInquiryResponse>>> getInquiries() {
        List<ContactInquiryResponse> inquiries = contactInquiryService.getInquiries();
        return ResponseEntity.ok(ApiResponse.success(inquiries));
    }

    @PostMapping("/super-admin/inquiries/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update Sales Lead Status", description = "Super Admin updates lead status (NEW, CONTACTED, CONVERTED, CLOSED)")
    public ResponseEntity<ApiResponse<ContactInquiryResponse>> updateInquiryStatus(@PathVariable UUID id, @RequestParam String status) {
        ContactInquiryResponse inquiry = contactInquiryService.updateInquiryStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Lead status updated to " + status, inquiry));
    }
}