package com.pamir.ppfarmsbackend.crops.controller;

import com.pamir.ppfarmsbackend.crops.dto.CropPlotRequest;
import com.pamir.ppfarmsbackend.crops.dto.CropPlotResponse;
import com.pamir.ppfarmsbackend.crops.dto.HarvestRequest;
import com.pamir.ppfarmsbackend.crops.dto.HarvestResponse;
import com.pamir.ppfarmsbackend.crops.service.CropService;
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
@RequestMapping("/api/v1/crops")
@RequiredArgsConstructor
@Tag(name = "Fodder & Crop Cultivation", description = "Endpoints for managing farm-grown green fodder (Napier, Maize, Lucerne, Sorghum, Silage plots) and harvest yields")
@SecurityRequirement(name = "Bearer Authentication")
public class CropController {

    private final CropService cropService;

    @PostMapping("/plots")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Register Crop Plot", description = "Creates a new farm land plot with crop type, sowing date, and expected harvest")
    public ResponseEntity<ApiResponse<CropPlotResponse>> createPlot(@RequestBody @Valid CropPlotRequest request,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CropPlotResponse response = cropService.createPlot(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Crop plot registered successfully", response));
    }

    @GetMapping("/plots")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Crop Plots", description = "Retrieves all crop cultivation plots with accumulated harvest yields")
    public ResponseEntity<ApiResponse<List<CropPlotResponse>>> getAllPlots(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CropPlotResponse> plots = cropService.getAllPlots(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(plots));
    }

    @GetMapping("/plots/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Crop Plot by ID", description = "Retrieves details of a specific crop plot")
    public ResponseEntity<ApiResponse<CropPlotResponse>> getPlotById(@PathVariable UUID id,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CropPlotResponse response = cropService.getPlotById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/plots/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Crop Plot", description = "Updates details for an existing crop plot")
    public ResponseEntity<ApiResponse<CropPlotResponse>> updatePlot(@PathVariable UUID id,
                                                                     @RequestBody @Valid CropPlotRequest request,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        CropPlotResponse response = cropService.updatePlot(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Crop plot updated successfully", response));
    }

    @DeleteMapping("/plots/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Crop Plot", description = "Deactivates a crop plot record")
    public ResponseEntity<ApiResponse<Void>> deletePlot(@PathVariable UUID id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        cropService.deletePlot(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Crop plot deleted successfully", null));
    }

    @PostMapping("/harvest")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'WORKER')")
    @Operation(summary = "Log Crop Harvest", description = "Records harvested green fodder yield in kg and automatically adds stock to feed inventory")
    public ResponseEntity<ApiResponse<HarvestResponse>> logHarvest(@RequestBody @Valid HarvestRequest request,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        HarvestResponse response = cropService.logHarvest(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Harvest logged and stock added to feed inventory", response));
    }

    @GetMapping("/harvest")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Harvest Logs", description = "Retrieves all crop harvest logs")
    public ResponseEntity<ApiResponse<List<HarvestResponse>>> getHarvestLogs(
            @RequestParam(required = false) UUID plotId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<HarvestResponse> logs = cropService.getHarvestLogs(userDetails.getTenantId(), plotId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
