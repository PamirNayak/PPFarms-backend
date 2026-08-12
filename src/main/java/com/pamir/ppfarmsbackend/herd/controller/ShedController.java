package com.pamir.ppfarmsbackend.herd.controller;

import com.pamir.ppfarmsbackend.herd.dto.ShedRequest;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.service.ShedService;
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
@RequestMapping("/api/v1/sheds")
@RequiredArgsConstructor
@Tag(name = "Shed & Pen Housing Management", description = "Endpoints for creating and listing farm sheds/pens")
@SecurityRequirement(name = "Bearer Authentication")
public class ShedController {

    private final ShedService shedService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Create Shed / Pen", description = "Registers a new housing shed or pen structure for livestock")
    public ResponseEntity<ApiResponse<ShedPen>> createShed(@RequestBody @Valid ShedRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        ShedPen shedPen = shedService.createShed(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shed/Pen created successfully", shedPen));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get All Sheds / Pens", description = "Retrieves all housing sheds and pens belonging to current farm")
    public ResponseEntity<ApiResponse<List<ShedPen>>> getSheds(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ShedPen> sheds = shedService.getSheds(userDetails);
        return ResponseEntity.ok(ApiResponse.success(sheds));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Update Shed / Pen", description = "Updates details for an existing housing shed/pen")
    public ResponseEntity<ApiResponse<ShedPen>> updateShed(@PathVariable UUID id,
                                                           @RequestBody @Valid ShedRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        ShedPen shedPen = shedService.updateShed(id, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Shed/Pen updated successfully", shedPen));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Shed / Pen", description = "Deactivates a housing shed/pen")
    public ResponseEntity<ApiResponse<Void>> deleteShed(@PathVariable UUID id,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        shedService.deleteShed(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Shed/Pen deleted successfully", null));
    }
}

