package com.pamir.ppfarmsbackend.health.controller;

import com.pamir.ppfarmsbackend.health.dto.BatchVaccinationRequest;
import com.pamir.ppfarmsbackend.health.dto.VaccinationRecordResponse;
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

@RestController
@RequestMapping("/api/v1/vaccinations")
@RequiredArgsConstructor
@Tag(name = "Vaccination Management", description = "Endpoints for batch vaccination administration and schedule tracking")
@SecurityRequirement(name = "Bearer Authentication")
public class VaccinationController {

    private final HealthService healthService;

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Administer Batch Vaccines", description = "Applies vaccine records to an entire shed/pen or a list of animal IDs simultaneously")
    public ResponseEntity<ApiResponse<List<VaccinationRecordResponse>>> administerBatchVaccine(@RequestBody @Valid BatchVaccinationRequest request,
                                                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<VaccinationRecordResponse> records = healthService.administerBatchVaccine(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Batch vaccination recorded for " + records.size() + " animals", records));
    }
}