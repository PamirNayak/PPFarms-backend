package com.pamir.ppfarmsbackend.flock.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class FlockBatchResponse {
    private UUID id;
    private UUID organizationId;
    private String batchName;
    private UUID speciesId;
    private String speciesName;
    private UUID shedPenId;
    private String shedPenName;
    private Integer initialQuantity;
    private Integer currentQuantity;
    private Integer totalMortality;
    private Double mortalityRatePercentage;
    private LocalDate arrivalDate;
    private Integer initialAgeWeeks;
    private String purpose;
    private String status;
    private BigDecimal purchaseCost;
    private String notes;
    private OffsetDateTime createdAt;
}
