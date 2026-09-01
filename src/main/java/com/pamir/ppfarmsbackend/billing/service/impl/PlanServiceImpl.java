package com.pamir.ppfarmsbackend.billing.service.impl;

import com.pamir.ppfarmsbackend.billing.dto.PlanRequest;
import com.pamir.ppfarmsbackend.billing.dto.PlanResponse;
import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.service.PlanService;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
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
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<PlanResponse> getActivePlans() {
        return planRepository.findByIsActiveTrue()
                .stream()
                .map(PlanResponse::fromEntity)
                .toList();
    }

    @Override
    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(PlanResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public PlanResponse createPlan(PlanRequest request) {
        Plan plan = Plan.builder()
                .name(request.getName())
                .planType(request.getPlanType())
                .price(request.getPrice())
                .maxAnimals(request.getMaxAnimals())
                .maxUsers(request.getMaxUsers())
                .features(request.getFeatures() != null && !request.getFeatures().isBlank() ? request.getFeatures() : "{}")
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return PlanResponse.fromEntity(planRepository.save(plan));
    }

    @Override
    @Transactional
    public PlanResponse updatePlan(UUID id, PlanRequest request) {
        Plan existing = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getPlanType() != null) existing.setPlanType(request.getPlanType());
        if (request.getPrice() != null) existing.setPrice(request.getPrice());
        if (request.getMaxAnimals() != null) existing.setMaxAnimals(request.getMaxAnimals());
        if (request.getMaxUsers() != null) existing.setMaxUsers(request.getMaxUsers());
        if (request.getFeatures() != null) existing.setFeatures(request.getFeatures());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());

        return PlanResponse.fromEntity(planRepository.save(existing));
    }

    @Override
    @Transactional
    public void deactivatePlan(UUID id) {
        Plan existing = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        existing.setIsActive(false);
        planRepository.save(existing);
    }
}