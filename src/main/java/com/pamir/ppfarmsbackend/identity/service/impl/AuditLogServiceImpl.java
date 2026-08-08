package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.entity.AuditLog;
import com.pamir.ppfarmsbackend.identity.repository.AuditLogRepository;
import com.pamir.ppfarmsbackend.identity.service.AuditLogService;
import com.pamir.ppfarmsbackend.shared.domain.PageResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public PageResponse<AuditLog> getAuditLogs(int page, int size, UUID organizationId, CustomUserDetails userDetails) {
        boolean isSuperAdmin = userDetails.getRole() != null && userDetails.getRole().contains("SUPER_ADMIN");
        Page<AuditLog> auditPage;

        if (isSuperAdmin && organizationId == null) {
            auditPage = auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        } else {
            UUID targetOrgId = isSuperAdmin && organizationId != null ? organizationId : userDetails.getTenantId();
            auditPage = auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(
                    targetOrgId, PageRequest.of(page, size));
        }

        return PageResponse.fromPage(auditPage);
    }
}
