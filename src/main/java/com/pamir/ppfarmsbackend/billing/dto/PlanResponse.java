package com.pamir.ppfarmsbackend.billing.dto;

import com.pamir.ppfarmsbackend.billing.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private UUID id;
    private String name;
    private String planType;
    private BigDecimal price;
    private Integer maxAnimals;
    private Integer maxUsers;
    private String features;
    private Boolean isActive;

    public static PlanResponse fromEntity(Plan plan) {
        if (plan == null) return null;
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .planType(plan.getPlanType())
                .price(plan.getPrice())
                .maxAnimals(plan.getMaxAnimals())
                .maxUsers(plan.getMaxUsers())
                .features(plan.getFeatures())
                .isActive(plan.getIsActive())
                .build();
    }
}