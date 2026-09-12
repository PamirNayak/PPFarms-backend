package com.pamir.ppfarmsbackend.herd.controller;

import com.pamir.ppfarmsbackend.herd.dto.BreedRequest;
import com.pamir.ppfarmsbackend.herd.dto.SpeciesRequest;
import com.pamir.ppfarmsbackend.herd.entity.Breed;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.herd.service.SpeciesBreedService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Species & Breed Master Registry", description = "Endpoints for managing species and breed registries")
@SecurityRequirement(name = "Bearer Authentication")
public class SpeciesBreedController {

    private final SpeciesBreedService speciesBreedService;

    @PostMapping("/species")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Species", description = "Registers a new livestock species category (e.g. Goat, Sheep, Cattle)")
    public ResponseEntity<ApiResponse<Species>> createSpecies(@RequestBody @Valid SpeciesRequest request,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Species species = speciesBreedService.createSpecies(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Species registered successfully", species));
    }

    @GetMapping("/species")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get All Species", description = "Retrieves list of available livestock species")
    public ResponseEntity<ApiResponse<List<Species>>> getSpecies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Species> speciesList = speciesBreedService.getAllSpecies(userDetails);
        return ResponseEntity.ok(ApiResponse.success(speciesList));
    }

    @PutMapping("/species/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Species", description = "Updates an existing species name or gestation duration")
    public ResponseEntity<ApiResponse<Species>> updateSpecies(@PathVariable UUID id,
                                                              @RequestBody @Valid SpeciesRequest request) {
        Species species = speciesBreedService.updateSpecies(id, request);
        return ResponseEntity.ok(ApiResponse.success("Species updated successfully", species));
    }

    @DeleteMapping("/species/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Species", description = "Deletes a species if no animals or breeds are currently assigned to it")
    public ResponseEntity<ApiResponse<Void>> deleteSpecies(@PathVariable UUID id) {
        speciesBreedService.deleteSpecies(id);
        return ResponseEntity.ok(ApiResponse.success("Species deleted successfully", null));
    }

    @PostMapping("/breeds")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create Breed", description = "Registers a new animal breed linked to a species")
    public ResponseEntity<ApiResponse<Breed>> createBreed(@RequestBody @Valid BreedRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Breed breed = speciesBreedService.createBreed(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Breed registered successfully", breed));
    }

    @GetMapping("/breeds")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Breeds by Species", description = "Retrieves breeds filtered by species ID")
    public ResponseEntity<ApiResponse<List<Breed>>> getBreeds(@RequestParam(required = false) UUID speciesId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Breed> breeds = speciesBreedService.getBreeds(speciesId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(breeds));
    }

    @PutMapping("/breeds/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Update Breed", description = "Updates an existing breed's name or description")
    public ResponseEntity<ApiResponse<Breed>> updateBreed(@PathVariable UUID id,
                                                          @RequestBody @Valid BreedRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Breed breed = speciesBreedService.updateBreed(id, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Breed updated successfully", breed));
    }

    @DeleteMapping("/breeds/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Breed", description = "Deletes a breed if no animals are actively assigned to it")
    public ResponseEntity<ApiResponse<Void>> deleteBreed(@PathVariable UUID id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        speciesBreedService.deleteBreed(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Breed deleted successfully", null));
    }
}

