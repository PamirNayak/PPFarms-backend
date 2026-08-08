package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.entity.UserNotificationPreference;
import com.pamir.ppfarmsbackend.identity.repository.UserNotificationPreferenceRepository;
import com.pamir.ppfarmsbackend.identity.service.NotificationPreferenceService;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Override
    public List<UserNotificationPreference> getPreferences(CustomUserDetails userDetails) {
        return preferenceRepository.findByUserId(userDetails.getId());
    }

    @Override
    @Transactional
    public UserNotificationPreference updatePreference(String notificationType, Boolean inAppEnabled, Boolean emailEnabled, CustomUserDetails userDetails) {
        Optional<UserNotificationPreference> existing = preferenceRepository.findByUserIdAndNotificationType(
                userDetails.getId(), notificationType);

        UserNotificationPreference pref = existing.orElseGet(() -> UserNotificationPreference.builder()
                .userId(userDetails.getId())
                .organizationId(userDetails.getTenantId())
                .notificationType(notificationType)
                .build());

        pref.setInAppEnabled(inAppEnabled);
        pref.setEmailEnabled(emailEnabled);
        return preferenceRepository.save(pref);
    }
}
