package com.pamir.ppfarmsbackend.identity.repository;

import com.pamir.ppfarmsbackend.identity.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByOrganizationIdAndUserIdOrUserIdIsNullOrderByCreatedAtDesc(UUID organizationId, UUID userId);
    List<Notification> findByOrganizationIdAndUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID organizationId, UUID userId);
}
