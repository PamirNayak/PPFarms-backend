package com.pamir.ppfarmsbackend.documents.controller;

import com.pamir.ppfarmsbackend.documents.dto.DocumentRequest;
import com.pamir.ppfarmsbackend.documents.dto.DocumentResponse;
import com.pamir.ppfarmsbackend.documents.service.DocumentService;
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
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Attachments & Digital Filing", description = "Endpoints for linking uploaded PDFs, veterinary health certificates, invoices, and animal registration papers to records")
@SecurityRequirement(name = "Bearer Authentication")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Attach Document", description = "Attaches an uploaded PDF or image URL to an Animal, Health record, Purchase, or Sale")
    public ResponseEntity<ApiResponse<DocumentResponse>> attachDocument(@RequestBody @Valid DocumentRequest request,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        DocumentResponse response = documentService.attachDocument(request, userDetails.getId(), userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document attached successfully", response));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Documents for Entity", description = "Retrieves all document attachments for a specific animal, purchase, or health event")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DocumentResponse> docs = documentService.getDocumentsByEntity(entityType, entityId, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "List All Farm Documents", description = "Retrieves all document attachments across the farm")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DocumentResponse> docs = documentService.getAllDocuments(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Delete Document Attachment", description = "Deletes a document attachment record")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable UUID id,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        documentService.deleteDocument(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }
}
