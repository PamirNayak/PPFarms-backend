package com.pamir.ppfarmsbackend.herd.controller;

import com.pamir.ppfarmsbackend.herd.dto.*;
import com.pamir.ppfarmsbackend.herd.service.AnimalService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.domain.PageResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
@Tag(name = "Livestock & Herd Management", description = "Endpoints for managing farm animals, pedigree trees, and weight growth")
@SecurityRequirement(name = "Bearer Authentication")
public class AnimalController {

    private final AnimalService animalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Register New Animal", description = "Creates an individual livestock record with species, breed, ear tag, and pedigree linkages")
    public ResponseEntity<ApiResponse<AnimalResponse>> createAnimal(@RequestBody @Valid AnimalRequest request,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        AnimalResponse response = animalService.createAnimal(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Animal registered successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Filter & List Animals", description = "Paginated search & multi-criteria filtering by species, breed, pen, gender, status, or ear tag")
    public ResponseEntity<ApiResponse<PageResponse<AnimalResponse>>> getAnimals(
            @RequestParam(required = false) UUID speciesId,
            @RequestParam(required = false) UUID breedId,
            @RequestParam(required = false) UUID shedPenId,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<AnimalResponse> animalsPage = animalService.filterAnimals(userDetails.getTenantId(), speciesId, breedId, shedPenId, gender, status, search, PageRequest.of(page, size, sort));

        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(animalsPage)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Animal Profile", description = "Retrieves full profile details for a specific animal")
    public ResponseEntity<ApiResponse<AnimalResponse>> getAnimalById(@PathVariable UUID id,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        AnimalResponse response = animalService.getAnimalById(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Update Animal Profile", description = "Updates details for an existing animal")
    public ResponseEntity<ApiResponse<AnimalResponse>> updateAnimal(@PathVariable UUID id,
                                                                      @RequestBody @Valid AnimalRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        AnimalResponse response = animalService.updateAnimal(id, request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Animal updated successfully", response));
    }

    @GetMapping("/{id}/pedigree")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get 3-Generation Pedigree Tree", description = "Returns nested parentage tree (Sire, Dam, Grand-Sires, Grand-Dams)")
    public ResponseEntity<ApiResponse<PedigreeTreeResponse>> getPedigreeTree(@PathVariable UUID id,
                                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        PedigreeTreeResponse response = animalService.getPedigreeTree(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/weight")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Log Weight Measurement", description = "Records a new scale weight measurement for growth tracking")
    public ResponseEntity<ApiResponse<Void>> logWeight(@PathVariable UUID id,
                                                        @RequestBody @Valid WeightRequest request,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        request.setAnimalId(id);
        animalService.logWeight(request, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Weight recorded successfully", null));
    }

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Full Lifecycle Timeline", description = "Retrieves unified chronological life event stream (birth, weights, vaccines, dewormings, treatments, matings, kiddings, sales)")
    public ResponseEntity<ApiResponse<java.util.List<AnimalTimelineEventDto>>> getAnimalTimeline(@PathVariable UUID id,
                                                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        java.util.List<AnimalTimelineEventDto> timeline = animalService.getAnimalTimeline(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }
}
