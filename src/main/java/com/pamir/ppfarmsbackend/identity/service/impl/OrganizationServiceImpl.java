package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.dto.OrganizationResponse;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.service.OrganizationService;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationResponse getOrganizationProfile(UUID tenantId) {
        Organization organization = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        return OrganizationResponse.fromEntity(organization);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganizationProfile(UUID tenantId, String name, String phone, String address, String currency) {
        Organization organization = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (name != null) organization.setName(name);
        if (phone != null) organization.setPhone(phone);
        if (address != null) organization.setAddress(address);
        if (currency != null) organization.setCurrency(currency);

        return OrganizationResponse.fromEntity(organizationRepository.save(organization));
    }
}