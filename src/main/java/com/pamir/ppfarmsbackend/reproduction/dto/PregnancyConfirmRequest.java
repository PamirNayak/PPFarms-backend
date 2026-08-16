package com.pamir.ppfarmsbackend.reproduction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PregnancyConfirmRequest {

    @NotNull(message = "Breeding record ID is required")
    private UUID breedingRecordId;

    @NotNull(message = "Confirmation date is required")
    private LocalDate confirmationDate;

    public UUID getBreedingRecordId() { return breedingRecordId; } public void setBreedingRecordId(UUID breedingRecordId) { this.breedingRecordId = breedingRecordId; }
    public LocalDate getConfirmationDate() { return confirmationDate; } public void setConfirmationDate(LocalDate confirmationDate) { this.confirmationDate = confirmationDate; }
}
