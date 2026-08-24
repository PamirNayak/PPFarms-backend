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
public class HarvestResponse {

    private UUID id;
    private UUID organizationId;
    private UUID cropPlotId;
    private String cropPlotName;
    private String cropName;
    private LocalDate harvestDate;
    private BigDecimal yieldKg;
    private UUID destinationFeedId;
    private String destinationFeedName;
    private String storageLocation;
    private String notes;
    private OffsetDateTime createdAt;
}
