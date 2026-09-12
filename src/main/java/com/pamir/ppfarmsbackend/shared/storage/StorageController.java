package com.pamir.ppfarmsbackend.shared.storage;

import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "Cloud Storage Management", description = "Unified endpoint for uploading and managing cloud images & documents")
@SecurityRequirement(name = "Bearer Authentication")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload Image or Document", description = "Uploads file to Supabase storage scoped by tenant and category")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "ANIMALS") StorageCategory category,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (category == StorageCategory.SYSTEM_QR) {
            boolean isSuperAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
            if (!isSuperAdmin) {
                throw new BadRequestException("Only Super Admin can upload system bank QR codes.");
            }
        }

        UUID tenantId = (category == StorageCategory.SYSTEM_QR) ? null : userDetails.getTenantId();
        String fileUrl = storageService.uploadFile(file, category, tenantId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded successfully", Map.of(
                        "url", fileUrl,
                        "category", category.name()
                )));
    }

    @GetMapping("/view")
    @Operation(summary = "View / Stream Cloud Storage File", description = "Streams raw binary file bytes from cloud storage with automatic MIME type headers")
    public ResponseEntity<byte[]> viewFile(
            @RequestParam("url") String fileUrl
    ) {
        byte[] fileBytes = storageService.downloadFileByUrl(fileUrl);
        String contentType = storageService.getContentType(fileUrl);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                .header(org.springframework.http.HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(fileBytes);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete File from Cloud Storage", description = "Deletes a previously uploaded file from Supabase storage")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestParam("url") String fileUrl
    ) {
        storageService.deleteFileByUrl(fileUrl);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }
}
