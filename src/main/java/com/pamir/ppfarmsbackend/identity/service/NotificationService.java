package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.NotificationResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationResponse> getNotifications(CustomUserDetails userDetails);
    NotificationResponse markAsRead(UUID id, CustomUserDetails userDetails);
    void markAllAsRead(CustomUserDetails userDetails);
}