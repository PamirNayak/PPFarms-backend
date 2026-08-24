package com.pamir.ppfarmsbackend.crops.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPlotResponse {

    private UUID id;
    private UUID organizationId;
    private String plotName;
    private BigDecimal areaAcres;
    private String cropName;
    private String variety;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private String status;
    private String irrigationType;
    private BigDecimal productionCost;
    private BigDecimal totalHarvestedYieldKg;
    private String notes;
    private OffsetDateTime createdAt;
}
