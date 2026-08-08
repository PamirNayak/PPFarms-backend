package com.pamir.ppfarmsbackend.identity.controller;

import com.pamir.ppfarmsbackend.identity.dto.NavigationMenuItemDto;
import com.pamir.ppfarmsbackend.identity.dto.UserRequest;
import com.pamir.ppfarmsbackend.identity.dto.UserResponse;
import com.pamir.ppfarmsbackend.identity.service.UserService;
import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Farm Staff Management & User Profile", description = "Endpoints for user profile settings and Farm Admins to invite and manage workers/vets")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Get Logged-in User Profile", description = "Retrieves profile details for current authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.getMyProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/navigation")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Get Role-Based Navigation Menu", description = "Computes authorized sidebar navigation links based on logged-in user role")
    public ResponseEntity<ApiResponse<List<NavigationMenuItemDto>>> getNavigationMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NavigationMenuItemDto> menu = userService.getNavigationMenu(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(menu));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Update Logged-in User Profile", description = "Updates name and phone for current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(@RequestBody UpdateProfileRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.updateMyProfile(userDetails.getId(), request.getName(), request.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PostMapping("/me/change-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'VET', 'WORKER', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Change Password", description = "Changes password for current authenticated user")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.changePassword(userDetails.getId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Invite Farm Staff", description = "Allows Farm Owner ADMIN or SUPER_ADMIN to create accounts for Workers or Vets")
    public ResponseEntity<ApiResponse<UserResponse>> inviteStaff(@RequestBody @Valid UserRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.inviteStaff(request, userDetails.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Staff user invited successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get All Farm Staff", description = "Retrieves list of all staff members belonging to the current farm")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFarmStaff(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserResponse> staffList = userService.getFarmStaff(userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(staffList));
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Toggle Staff Status", description = "Activates or deactivates a staff member's login access")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable UUID id,
                                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.toggleUserStatus(id, userDetails.getTenantId());
        return ResponseEntity.ok(ApiResponse.success("Staff status updated", response));
    }

    @Data
    public static class UpdateProfileRequest {
        private String name;
        private String phone;
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        public String getOldPassword() { return oldPassword; } public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; } public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}

