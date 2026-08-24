package com.pamir.ppfarmsbackend.flock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EggLogRequest {
    @NotNull(message = "Flock Batch ID is required")
    private UUID flockBatchId;

    @NotNull(message = "Collection date is required")
    private LocalDate collectionDate;

    @NotNull(message = "Total eggs collected is required")
    @Min(value = 1, message = "Total eggs must be at least 1")
    private Integer totalEggs;

    private Integer brokenEggs;
    private Integer traysCount;
    private String notes;
}
