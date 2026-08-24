package com.pamir.ppfarmsbackend.flock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class FlockBatchRequest {
    @NotBlank(message = "Batch name is required (e.g. Desi Chicks Batch #1)")
    private String batchName;

    @NotNull(message = "Species ID is required")
    private UUID speciesId;

    private UUID shedPenId;

    @NotNull(message = "Initial bird quantity is required")
    private Integer initialQuantity;

    @NotNull(message = "Arrival date is required")
    private LocalDate arrivalDate;

    private Integer initialAgeWeeks;
    private String purpose; // MEAT, EGGS, DUAL, BREEDING
    private String status;  // ACTIVE, SOLD, COMPLETED
    private BigDecimal purchaseCost;
    private String notes;
}
