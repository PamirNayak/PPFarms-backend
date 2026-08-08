package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.entity.AuditLog;
import com.pamir.ppfarmsbackend.shared.domain.PageResponse;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;

import java.util.UUID;

public interface AuditLogService {
    PageResponse<AuditLog> getAuditLogs(int page, int size, UUID organizationId, CustomUserDetails userDetails);
}
