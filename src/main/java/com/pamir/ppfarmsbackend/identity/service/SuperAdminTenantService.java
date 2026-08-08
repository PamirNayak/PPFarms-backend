package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.TenantSummaryDto;

import java.util.List;
import java.util.UUID;

public interface SuperAdminTenantService {
    List<TenantSummaryDto> getAllTenants(String search, String status);
    TenantSummaryDto getTenantDetails(UUID tenantId);
    TenantSummaryDto toggleTenantStatus(UUID tenantId);
    TenantSummaryDto extendTrial(UUID tenantId, int extraDays);
    TenantSummaryDto assignPlan(UUID tenantId, UUID planId, int durationDays);
}
