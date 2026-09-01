package com.pamir.ppfarmsbackend.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {
    @NotBlank(message = "Plan name is required")
    private String name;

    @NotBlank(message = "Plan type is required (MONTHLY/YEARLY)")
    private String planType;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Max animals limit is required")
    private Integer maxAnimals;

    @NotNull(message = "Max users limit is required")
    private Integer maxUsers;

    private String features;
    private Boolean isActive;
}