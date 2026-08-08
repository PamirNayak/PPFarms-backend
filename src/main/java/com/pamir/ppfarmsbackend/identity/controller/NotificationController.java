package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.NotificationResponse;
import com.pamir.ppfarmsbackend.identity.service.NotificationService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "In-App Dashboard Notifications", description = "Endpoints for viewing and marking dashboard bell notifications as read")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Get Dashboard Notifications", description = "Retrieves in-app bell notifications for current logged-in user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponse> notifications = notificationService.getNotifications(userDetails);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Mark Notification as Read", description = "Sets notification isRead status to true")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id,
                                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationResponse notification = notificationService.markAsRead(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Mark All Notifications as Read", description = "Sets all unread notifications to read for current user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}