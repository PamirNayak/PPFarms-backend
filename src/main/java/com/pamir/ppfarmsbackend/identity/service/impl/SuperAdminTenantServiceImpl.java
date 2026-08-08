package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.identity.dto.TenantSummaryDto;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.identity.service.SuperAdminTenantService;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminTenantServiceImpl implements SuperAdminTenantService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final AnimalRepository animalRepository;

    private static final UUID SYSTEM_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    @Transactional(readOnly = true)
    public List<TenantSummaryDto> getAllTenants(String search, String status) {
        List<Organization> orgs = organizationRepository.findAll().stream()
                .filter(o -> !SYSTEM_ORG_ID.equals(o.getId()))
                .toList();

        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            orgs = orgs.stream()
                    .filter(o -> status.equalsIgnoreCase(o.getStatus()))
                    .toList();
        }

        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();
            orgs = orgs.stream()
                    .filter(o -> (o.getName() != null && o.getName().toLowerCase().contains(lower)) ||
                            (o.getEmail() != null && o.getEmail().toLowerCase().contains(lower)) ||
                            (o.getPhone() != null && o.getPhone().contains(lower)))
                    .toList();
        }

        return orgs.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::buildTenantSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TenantSummaryDto getTenantDetails(UUID tenantId) {
        Organization org = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm organization not found"));
        return buildTenantSummary(org);
    }

    @Override
    @Transactional
    public TenantSummaryDto toggleTenantStatus(UUID tenantId) {
        Organization org = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm organization not found"));

        if ("ACTIVE".equalsIgnoreCase(org.getStatus())) {
            org.setStatus("SUSPENDED");
        } else {
            org.setStatus("ACTIVE");
        }

        org = organizationRepository.save(org);
        return buildTenantSummary(org);
    }

    @Override
    @Transactional
    public TenantSummaryDto extendTrial(UUID tenantId, int extraDays) {
        Organization org = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm organization not found"));

        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElseGet(() -> {
                    Plan freePlan = planRepository.findAll().stream()
                            .filter(p -> "FREE".equalsIgnoreCase(p.getPlanType()))
                            .findFirst()
                            .orElse(null);
                    return Subscription.builder()
                            .organizationId(tenantId)
                            .plan(freePlan)
                            .status("TRIAL")
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(extraDays))
                            .build();
                });

        LocalDate currentEnd = sub.getEndDate() != null && sub.getEndDate().isAfter(LocalDate.now())
                ? sub.getEndDate()
                : LocalDate.now();

        sub.setEndDate(currentEnd.plusDays(extraDays));
        if (!"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
            sub.setStatus("TRIAL");
        }
        subscriptionRepository.save(sub);

        return buildTenantSummary(org);
    }

    @Override
    @Transactional
    public TenantSummaryDto assignPlan(UUID tenantId, UUID planId, int durationDays) {
        Organization org = organizationRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm organization not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElseGet(() -> Subscription.builder()
                        .organizationId(tenantId)
                        .build());

        sub.setPlan(plan);
        sub.setStatus("ACTIVE");
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusDays(durationDays > 0 ? durationDays : 30));
        subscriptionRepository.save(sub);

        return buildTenantSummary(org);
    }

    private TenantSummaryDto buildTenantSummary(Organization org) {
        // Find owner admin user
        List<User> users = userRepository.findByOrganizationIdAndDeletedAtIsNull(org.getId());
        User owner = users.stream()
                .filter(u -> u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole().getName()))
                .findFirst()
                .orElse(users.isEmpty() ? null : users.get(0));

        // Find subscription
        Subscription sub = subscriptionRepository.findByOrganizationId(org.getId()).orElse(null);

        long daysRemaining = 0;
        if (sub != null && sub.getEndDate() != null && !sub.getEndDate().isBefore(LocalDate.now())) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate());
        }

        long totalAnimals = animalRepository.countByOrganizationIdAndDeletedAtIsNull(org.getId());
        long totalStaff = users.size();

        return TenantSummaryDto.builder()
                .id(org.getId())
                .farmName(org.getName())
                .email(org.getEmail())
                .phone(org.getPhone())
                .address(org.getAddress())
                .currency(org.getCurrency() != null ? org.getCurrency() : "INR")
                .status(org.getStatus() != null ? org.getStatus() : "ACTIVE")
                .ownerId(owner != null ? owner.getId() : null)
                .ownerName(owner != null ? owner.getName() : "N/A")
                .ownerEmail(owner != null ? owner.getEmail() : org.getEmail())
                .ownerPhone(owner != null ? owner.getPhone() : org.getPhone())
                .planId(sub != null && sub.getPlan() != null ? sub.getPlan().getId() : null)
                .planName(sub != null && sub.getPlan() != null ? sub.getPlan().getName() : "No Plan")
                .planType(sub != null && sub.getPlan() != null ? sub.getPlan().getPlanType() : "N/A")
                .subscriptionStatus(sub != null ? sub.getStatus() : "NO_SUBSCRIPTION")
                .subscriptionStartDate(sub != null ? sub.getStartDate() : null)
                .subscriptionEndDate(sub != null ? sub.getEndDate() : null)
                .daysRemaining(daysRemaining)
                .totalAnimals(totalAnimals)
                .totalStaff(totalStaff)
                .createdAt(org.getCreatedAt())
                .build();
    }
}
