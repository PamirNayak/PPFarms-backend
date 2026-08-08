package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.entity.UserNotificationPreference;
import com.pamir.ppfarmsbackend.identity.service.NotificationPreferenceService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/notification-preferences")
@RequiredArgsConstructor
@Tag(name = "Per-Admin Notification Channel Preferences", description = "Endpoints for configuring In-App Bell and Email alerts toggle switches per user")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Get Notification Preferences", description = "Retrieves toggle switch preferences matrix for logged in user")
    public ResponseEntity<ApiResponse<List<UserNotificationPreference>>> getPreferences(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserNotificationPreference> prefs = preferenceService.getPreferences(userDetails);
        return ResponseEntity.ok(ApiResponse.success(prefs));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'VET', 'WORKER')")
    @Operation(summary = "Update Notification Preferences", description = "Toggles In-App Bell and Email alerts for a specific notification type")
    public ResponseEntity<ApiResponse<UserNotificationPreference>> updatePreference(
            @RequestBody @Valid PreferenceUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserNotificationPreference pref = preferenceService.updatePreference(
                request.getNotificationType(), request.getInAppEnabled(), request.getEmailEnabled(), userDetails);

        return ResponseEntity.ok(ApiResponse.success("Notification preferences updated", pref));
    }

    @Data
    public static class PreferenceUpdateRequest {
        private String notificationType;
        private Boolean inAppEnabled;
        private Boolean emailEnabled;

        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        public Boolean getInAppEnabled() { return inAppEnabled; }
        public void setInAppEnabled(Boolean inAppEnabled) { this.inAppEnabled = inAppEnabled; }
        public Boolean getEmailEnabled() { return emailEnabled; }
        public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    }
}
