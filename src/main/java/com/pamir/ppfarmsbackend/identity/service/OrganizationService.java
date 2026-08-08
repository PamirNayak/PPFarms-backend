package com.pamir.ppfarmsbackend.identity.service;

import com.pamir.ppfarmsbackend.identity.dto.OrganizationResponse;

import java.util.UUID;

public interface OrganizationService {
    OrganizationResponse getOrganizationProfile(UUID tenantId);
    OrganizationResponse updateOrganizationProfile(UUID tenantId, String name, String phone, String address, String currency);
}