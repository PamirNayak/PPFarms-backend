package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.entity.UserNotificationPreference;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;

import java.util.List;

public interface NotificationPreferenceService {
    List<UserNotificationPreference> getPreferences(CustomUserDetails userDetails);
    UserNotificationPreference updatePreference(String notificationType, Boolean inAppEnabled, Boolean emailEnabled, CustomUserDetails userDetails);
}
