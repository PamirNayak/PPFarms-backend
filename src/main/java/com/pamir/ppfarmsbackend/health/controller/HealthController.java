package com.pamir.ppfarmsbackend.health.controller;

import com.pamir.ppfarmsbackend.health.dto.*;
import com.pamir.ppfarmsbackend.health.service.HealthService;
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
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Veterinary & Health Management", description = "Endpoints for logging medical treatments, diagnoses, vaccinations, dewormings, and mortalities")
@SecurityRequirement(name = "Bearer Authentication")
public class HealthController {

    private final HealthService healthService;

    @PostMapping("/treatments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Medical Treatment", description = "Records symptoms, treatment, vet info, and drug withdrawal dates for safety compliance")
    public ResponseEntity<ApiResponse<HealthRecordResponse>> logTreatment(@RequestBody @Valid HealthRequest request,
                                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        HealthRecordResponse record = healthService.logHealthTreatment(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medical treatment logged successfully", record));
    }

    @GetMapping("/treatments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Animal Health History", description = "Retrieves past medical treatment records for an animal")
    public ResponseEntity<ApiResponse<List<HealthRecordResponse>>> getHealthHistory(@RequestParam(required = false) UUID animalId,
                                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<HealthRecordResponse> history = healthService.getAnimalHealthHistory(animalId, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/vaccinations/batch")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Administer Batch Vaccine", description = "Records vaccination for multiple animals or a whole shed/pen")
    public ResponseEntity<ApiResponse<List<VaccinationRecordResponse>>> administerBatchVaccine(
            @RequestBody @Valid BatchVaccinationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<VaccinationRecordResponse> records = healthService.administerBatchVaccine(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Batch vaccination administered successfully (" + records.size() + " animals)", records));
    }

    @GetMapping("/vaccinations")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Vaccination History", description = "Retrieves farm-wide vaccination records")
    public ResponseEntity<ApiResponse<List<VaccinationRecordResponse>>> getVaccinationHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<VaccinationRecordResponse> records = healthService.getVaccinationHistory(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/vaccinations/upcoming")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Upcoming Vaccinations Due", description = "Retrieves vaccinations due within the next N days (default 30)")
    public ResponseEntity<ApiResponse<List<VaccinationRecordResponse>>> getUpcomingVaccinations(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<VaccinationRecordResponse> records = healthService.getUpcomingVaccinations(userDetails.getTenantId(), days);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping("/dewormings")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Administer Deworming", description = "Records deworming for multiple animals or a shed/pen with next due date")
    public ResponseEntity<ApiResponse<List<DewormingRecordResponse>>> administerDeworming(
            @RequestBody @Valid DewormingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DewormingRecordResponse> records = healthService.administerDeworming(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deworming administered successfully (" + records.size() + " animals)", records));
    }

    @GetMapping("/dewormings")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Deworming History", description = "Retrieves farm-wide deworming records")
    public ResponseEntity<ApiResponse<List<DewormingRecordResponse>>> getDewormingHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DewormingRecordResponse> records = healthService.getDewormingHistory(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/dewormings/upcoming")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Upcoming Dewormings Due", description = "Retrieves dewormings due within the next N days (default 30)")
    public ResponseEntity<ApiResponse<List<DewormingRecordResponse>>> getUpcomingDewormings(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DewormingRecordResponse> records = healthService.getUpcomingDewormings(userDetails.getTenantId(), days);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping("/mortality")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET')")
    @Operation(summary = "Log Animal Mortality", description = "Records animal death with cause and necropsy details, automatically marking animal status as DECEASED")
    public ResponseEntity<ApiResponse<MortalityRecordResponse>> logMortality(
            @RequestBody @Valid MortalityRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MortalityRecordResponse record = healthService.logMortality(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mortality record logged and animal status updated to DECEASED", record));
    }

    @GetMapping("/mortality")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Mortality Records", description = "Retrieves farm-wide mortality records")
    public ResponseEntity<ApiResponse<List<MortalityRecordResponse>>> getMortalityRecords(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MortalityRecordResponse> records = healthService.getMortalityRecords(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}