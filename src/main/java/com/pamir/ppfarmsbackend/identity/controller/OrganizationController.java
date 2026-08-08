package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.OrganizationResponse;
import com.pamir.ppfarmsbackend.identity.service.OrganizationService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations/me")
@RequiredArgsConstructor
@Tag(name = "Organization & Farm Settings Profile", description = "Endpoints for farm admins to view and update farm organization settings")
@SecurityRequirement(name = "Bearer Authentication")
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Farm Organization Details", description = "Retrieves profile details for current logged-in farm organization")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        OrganizationResponse organization = organizationService.getOrganizationProfile(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(organization));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Farm Organization Profile", description = "Updates farm name, phone, address, currency, and settings")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganizationProfile(
            @RequestBody @Valid UpdateOrganizationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrganizationResponse organization = organizationService.updateOrganizationProfile(
                userDetails.getTenantId(), request.getName(), request.getPhone(), request.getAddress(), request.getCurrency());
        return ResponseEntity.ok(ApiResponse.success("Organization profile updated successfully", organization));
    }

    @Data
    public static class UpdateOrganizationRequest {
        private String name;
        private String phone;
        private String address;
        private String currency;

        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; } public void setAddress(String address) { this.address = address; }
        public String getCurrency() { return currency; } public void setCurrency(String currency) { this.currency = currency; }
    }
}