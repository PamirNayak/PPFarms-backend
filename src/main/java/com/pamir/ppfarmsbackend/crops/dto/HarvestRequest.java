package com.pamir.ppfarmsbackend.crops.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRequest {

    @NotNull(message = "Crop plot ID is required")
    private UUID cropPlotId;

    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;

    @NotNull(message = "Yield in kg is required")
    private BigDecimal yieldKg;

    private UUID destinationFeedId;
    private String storageLocation;
    private String notes;
}
