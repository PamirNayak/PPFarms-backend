package com.pamir.ppfarmsbackend.reproduction.controller;

import com.pamir.ppfarmsbackend.reproduction.dto.*;
import com.pamir.ppfarmsbackend.reproduction.service.ReproductionService;
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
@RequestMapping("/api/v1/breeding")
@RequiredArgsConstructor
@Tag(name = "Breeding & Pregnancy Gestation", description = "Endpoints for logging mating events and confirming pregnancies")
@SecurityRequirement(name = "Bearer Authentication")
public class BreedingController {

    private final ReproductionService reproductionService;

    @PostMapping("/mating")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Mating / Breeding Event", description = "Records a natural or artificial insemination mating between Sire and Dam")
    public ResponseEntity<ApiResponse<BreedingRecordResponse>> logMating(@RequestBody @Valid BreedingRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        BreedingRecordResponse record = reproductionService.logBreeding(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Breeding event logged successfully", record));
    }

    @GetMapping("/mating/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Pending Mating Events", description = "Lists mating events awaiting pregnancy confirmation")
    public ResponseEntity<ApiResponse<List<BreedingRecordResponse>>> getPendingMatings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<BreedingRecordResponse> matings = reproductionService.getPendingMatings(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(matings));
    }

    @PostMapping("/pregnancies/confirm")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Confirm Pregnancy", description = "Confirms pregnancy and automatically calculates expected due date based on species gestation days")
    public ResponseEntity<ApiResponse<PregnancyResponse>> confirmPregnancy(@RequestBody @Valid PregnancyConfirmRequest request,
                                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        PregnancyResponse pregnancy = reproductionService.confirmPregnancy(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pregnancy confirmed. Expected due date: " + pregnancy.getExpectedDueDate(), pregnancy));
    }

    @GetMapping("/pregnancies/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Active Pregnancies", description = "Lists all confirmed active pregnancies for the farm")
    public ResponseEntity<ApiResponse<List<PregnancyResponse>>> getActivePregnancies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PregnancyResponse> pregnancies = reproductionService.getActivePregnancies(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(pregnancies));
    }
}