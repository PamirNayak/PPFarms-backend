package com.pamir.ppfarmsbackend.flock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MortalityLogRequest {
    @NotNull(message = "Flock Batch ID is required")
    private UUID flockBatchId;

    @NotNull(message = "Dead count is required")
    @Min(value = 1, message = "Dead count must be at least 1")
    private Integer deadCount;

    @NotNull(message = "Log date is required")
    private LocalDate logDate;

    private String causeOfDeath;
    private String notes;
}
