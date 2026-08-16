package com.pamir.ppfarmsbackend.production.controller;

import com.pamir.ppfarmsbackend.production.dto.ProductionRecordRequest;
import com.pamir.ppfarmsbackend.production.dto.ProductionRecordResponse;
import com.pamir.ppfarmsbackend.production.service.ProductionService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/production")
@RequiredArgsConstructor
@Tag(name = "Production Management", description = "Endpoints for logging daily milk yields (Morning/Evening) and wool/fiber harvests")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductionController {

    private final ProductionService productionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Production Yield", description = "Records milk (morning/evening liters, fat %, SNF %) or wool production for individual animals or bulk sheds")
    public ResponseEntity<ApiResponse<ProductionRecordResponse>> logProduction(@RequestBody @Valid ProductionRecordRequest request,
                                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProductionRecordResponse response = productionService.logProduction(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Production yield logged successfully", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Bulk Production Yields", description = "Records multiple milk yield entries in a single batch transaction")
    public ResponseEntity<ApiResponse<List<ProductionRecordResponse>>> logBulkProduction(
            @RequestBody List<@Valid ProductionRecordRequest> requests,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductionRecordResponse> response = productionService.logBulkProduction(requests, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk production yields logged successfully (" + response.size() + " records)", response));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Production History", description = "Retrieves farm-wide production records within a specified date range")
    public ResponseEntity<ApiResponse<List<ProductionRecordResponse>>> getProductionHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductionRecordResponse> history = productionService.getProductionHistory(userDetails.getTenantId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Individual Animal Production", description = "Retrieves production log history for a specific animal")
    public ResponseEntity<ApiResponse<List<ProductionRecordResponse>>> getAnimalProduction(@PathVariable UUID animalId,
                                                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductionRecordResponse> history = productionService.getAnimalProduction(animalId, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
