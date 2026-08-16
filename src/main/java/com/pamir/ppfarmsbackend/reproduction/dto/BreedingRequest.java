package com.pamir.ppfarmsbackend.reproduction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BreedingRequest {

    @NotNull(message = "Dam (mother) ID is required")
    private UUID damId;

    @NotNull(message = "Sire (father) ID is required")
    private UUID sireId;

    @NotBlank(message = "Breeding type is required")
    private String breedingType; // NATURAL, ARTIFICIAL_INSEMINATION

    @NotNull(message = "Mating date is required")
    private LocalDate bredAt;

    public UUID getDamId() { return damId; } public void setDamId(UUID damId) { this.damId = damId; }
    public UUID getSireId() { return sireId; } public void setSireId(UUID sireId) { this.sireId = sireId; }
    public String getBreedingType() { return breedingType; } public void setBreedingType(String breedingType) { this.breedingType = breedingType; }
    public LocalDate getBredAt() { return bredAt; } public void setBredAt(LocalDate bredAt) { this.bredAt = bredAt; }
}
