package com.pamir.ppfarmsbackend.identity.repository;

import com.pamir.ppfarmsbackend.identity.entity.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    List<UserNotificationPreference> findByUserId(UUID userId);
    Optional<UserNotificationPreference> findByUserIdAndNotificationType(UUID userId, String notificationType);
}
