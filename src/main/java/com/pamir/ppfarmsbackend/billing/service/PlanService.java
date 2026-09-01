package com.pamir.ppfarmsbackend.billing.service;

import com.pamir.ppfarmsbackend.billing.dto.PlanRequest;
import com.pamir.ppfarmsbackend.billing.dto.PlanResponse;

import java.util.List;
import java.util.UUID;

public interface PlanService {
    List<PlanResponse> getActivePlans();
    List<PlanResponse> getAllPlans();
    PlanResponse createPlan(PlanRequest plan);
    PlanResponse updatePlan(UUID id, PlanRequest request);
    void deactivatePlan(UUID id);
}