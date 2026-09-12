package com.pamir.ppfarmsbackend.shared.service.impl;

import com.pamir.ppfarmsbackend.health.domain.HealthStatus;
import com.pamir.ppfarmsbackend.herd.domain.AnimalGender;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.reproduction.domain.BreedingType;
import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryRequest;
import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryResponse;
import com.pamir.ppfarmsbackend.shared.entity.SystemReferenceCategory;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.repository.SystemReferenceCategoryRepository;
import com.pamir.ppfarmsbackend.shared.service.ReferenceMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReferenceMetadataServiceImpl implements ReferenceMetadataService {

    private final SystemReferenceCategoryRepository categoryRepository;

    @Override
    public Map<String, Object> getFullReferenceMetadata() {
        Map<String, Object> metadata = new HashMap<>();

        // 1. Export Backend Java Enums to Frontend
        metadata.put("ANIMAL_GENDER", Arrays.stream(AnimalGender.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("ANIMAL_STATUS", Arrays.stream(AnimalStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("HEALTH_STATUS", Arrays.stream(HealthStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("BREEDING_TYPE", Arrays.stream(BreedingType.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("PAYMENT_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.billing.domain.PaymentStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("PAYMENT_METHOD", Arrays.stream(com.pamir.ppfarmsbackend.billing.domain.PaymentMethod.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("SUBSCRIPTION_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.billing.domain.SubscriptionStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("PLAN_TYPE", Arrays.stream(com.pamir.ppfarmsbackend.billing.domain.PlanType.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("TASK_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.tasks.domain.TaskStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("TASK_PRIORITY", Arrays.stream(com.pamir.ppfarmsbackend.tasks.domain.TaskPriority.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("SALE_PAYMENT_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.sales.domain.SalePaymentStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("PREGNANCY_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.reproduction.domain.PregnancyStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("VACCINATION_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.health.domain.VaccinationStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("FLOCK_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.flock.domain.FlockStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("ACCOUNT_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.identity.domain.AccountStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        metadata.put("INQUIRY_STATUS", Arrays.stream(com.pamir.ppfarmsbackend.identity.domain.InquiryStatus.values())
                .map(e -> Map.of("value", e.name(), "label", e.getDisplayName()))
                .toList());

        // 2. Export Available SaaS System Modules Catalog
        metadata.put("SYSTEM_MODULES", com.pamir.ppfarmsbackend.billing.domain.PlanFeature.toCatalogList());

        // 3. Export DB Categories
        List<SystemReferenceCategory> allActive = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        Map<String, List<Map<String, String>>> dbCategories = new HashMap<>();

        for (SystemReferenceCategory item : allActive) {
            dbCategories.computeIfAbsent(item.getCategoryType(), k -> new ArrayList<>())
                    .add(Map.of("value", item.getCode(), "label", item.getDisplayLabel()));
        }

        metadata.put("dbCategories", dbCategories);
        return metadata;
    }

    @Override
    public List<Map<String, String>> getSystemModules() {
        return com.pamir.ppfarmsbackend.billing.domain.PlanFeature.toCatalogList();
    }

    @Override
    public Map<String, List<ReferenceCategoryResponse>> getReferenceCategories() {
        List<SystemReferenceCategory> allActive = categoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        Map<String, List<ReferenceCategoryResponse>> grouped = new HashMap<>();

        for (SystemReferenceCategory item : allActive) {
            grouped.computeIfAbsent(item.getCategoryType(), k -> new ArrayList<>())
                    .add(ReferenceCategoryResponse.fromEntity(item));
        }

        return grouped;
    }

    @Override
    @Transactional
    public ReferenceCategoryResponse createCategory(ReferenceCategoryRequest request) {
        SystemReferenceCategory category = SystemReferenceCategory.builder()
                .categoryType(request.getCategoryType())
                .code(request.getCode())
                .displayLabel(request.getDisplayLabel())
                .description(request.getDescription())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return ReferenceCategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public ReferenceCategoryResponse updateCategory(UUID id, ReferenceCategoryRequest details) {
        SystemReferenceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System reference category not found with id " + id));

        if (details.getCategoryType() != null) category.setCategoryType(details.getCategoryType());
        if (details.getCode() != null) category.setCode(details.getCode());
        if (details.getDisplayLabel() != null) category.setDisplayLabel(details.getDisplayLabel());
        if (details.getDescription() != null) category.setDescription(details.getDescription());
        if (details.getSortOrder() != null) category.setSortOrder(details.getSortOrder());
        if (details.getIsActive() != null) category.setIsActive(details.getIsActive());

        return ReferenceCategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("System reference category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }
}