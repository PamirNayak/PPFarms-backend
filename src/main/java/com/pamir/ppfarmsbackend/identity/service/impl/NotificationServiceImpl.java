package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.dto.NotificationResponse;
import com.pamir.ppfarmsbackend.identity.entity.Notification;
import com.pamir.ppfarmsbackend.identity.repository.NotificationRepository;
import com.pamir.ppfarmsbackend.identity.service.NotificationService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getNotifications(CustomUserDetails userDetails) {
        return notificationRepository.findByOrganizationIdAndUserIdOrUserIdIsNullOrderByCreatedAtDesc(
                userDetails.getTenantId(), userDetails.getId())
                .stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID id, CustomUserDetails userDetails) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));

        if (!notification.getOrganizationId().equals(userDetails.getTenantId())) {
            throw new BadRequestException("Unauthorized access to notification");
        }

        notification.setIsRead(true);
        return NotificationResponse.fromEntity(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(CustomUserDetails userDetails) {
        List<Notification> unreadList = notificationRepository.findByOrganizationIdAndUserIdAndIsReadFalseOrderByCreatedAtDesc(
                userDetails.getTenantId(), userDetails.getId());
        for (Notification n : unreadList) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }
}