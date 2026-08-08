package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.entity.AuditLog;
import com.pamir.ppfarmsbackend.identity.service.AuditLogService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.domain.PageResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Trail & Activity Logs", description = "Endpoints for Farm Admins to view security and operational audit trail of staff actions")
@SecurityRequirement(name = "Bearer Authentication")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get Farm Audit Trail (Paginated)", description = "Lists chronological audit activity logs of who modified/deleted records")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PageResponse<AuditLog> response = auditLogService.getAuditLogs(page, size, organizationId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

