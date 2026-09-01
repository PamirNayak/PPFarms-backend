package com.pamir.ppfarmsbackend.shared.controller;

import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryRequest;
import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryResponse;
import com.pamir.ppfarmsbackend.shared.service.ReferenceMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "System Reference Categories & Metadata", description = "Endpoints for retrieving system-wide Java Enums, database reference categories, and Super Admin metadata management")
public class ReferenceMetadataController {

    private final ReferenceMetadataService referenceMetadataService;

    @GetMapping("/reference/metadata")
    @Operation(summary = "Get Full Reference Metadata (Enums + DB Categories)", description = "Returns system Java Enums and database reference categories for dynamic frontend dropdowns")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFullReferenceMetadata() {
        return ResponseEntity.ok(ApiResponse.success(referenceMetadataService.getFullReferenceMetadata()));
    }

    @GetMapping("/reference/system-modules")
    @Operation(summary = "Get System SaaS Modules Catalog", description = "Returns dynamic list of all software modules available for plan packaging")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getSystemModules() {
        return ResponseEntity.ok(ApiResponse.success(referenceMetadataService.getSystemModules()));
    }

    @GetMapping("/reference/categories")
    @Operation(summary = "Get Database System Reference Categories", description = "Returns active database reference categories grouped by categoryType")
    public ResponseEntity<ApiResponse<Map<String, List<ReferenceCategoryResponse>>>> getReferenceCategories() {
        return ResponseEntity.ok(ApiResponse.success(referenceMetadataService.getReferenceCategories()));
    }

    @PostMapping("/super-admin/reference/categories")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create System Reference Category", description = "Super Admin adds new reference category row to database")
    public ResponseEntity<ApiResponse<ReferenceCategoryResponse>> createCategory(@RequestBody @Valid ReferenceCategoryRequest category) {
        ReferenceCategoryResponse saved = referenceMetadataService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("System reference category created successfully", saved));
    }

    @PutMapping("/super-admin/reference/categories/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update System Reference Category", description = "Super Admin updates display label, sort order, or active status")
    public ResponseEntity<ApiResponse<ReferenceCategoryResponse>> updateCategory(@PathVariable UUID id,
                                                                                 @RequestBody @Valid ReferenceCategoryRequest details) {
        ReferenceCategoryResponse updated = referenceMetadataService.updateCategory(id, details);
        return ResponseEntity.ok(ApiResponse.success("System reference category updated", updated));
    }

    @DeleteMapping("/super-admin/reference/categories/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete System Reference Category", description = "Super Admin deletes a reference category row from database")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        referenceMetadataService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("System reference category deleted successfully", null));
    }
}