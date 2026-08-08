package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.NavigationMenuItemDto;
import com.pamir.ppfarmsbackend.identity.dto.UserRequest;
import com.pamir.ppfarmsbackend.identity.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getMyProfile(UUID userId);
    List<NavigationMenuItemDto> getNavigationMenu(UUID userId);
    UserResponse updateMyProfile(UUID userId, String name, String phone);
    void changePassword(UUID userId, String oldPassword, String newPassword);
    UserResponse inviteStaff(UserRequest request, UUID tenantId);
    List<UserResponse> getFarmStaff(UUID tenantId);
    UserResponse toggleUserStatus(UUID userId, UUID tenantId);
}
