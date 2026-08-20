package com.pamir.ppfarmsbackend.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class HealthRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    @NotBlank(message = "Symptoms description is required")
    private String symptoms;

    @NotBlank(message = "Treatment details are required")
    private String treatment;

    @NotBlank(message = "Health status is required")
    private String healthStatus; // UNDER_TREATMENT, RECOVERED, CRITICAL, CHRONIC

    private String vetName;

    @NotNull(message = "Treatment date is required")
    private LocalDate treatmentDate;

    private LocalDate followUpDate;
    private LocalDate milkWithdrawalUntilDate;
    private LocalDate slaughterWithdrawalUntilDate;

    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public String getSymptoms() { return symptoms; } public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getTreatment() { return treatment; } public void setTreatment(String treatment) { this.treatment = treatment; }
    public String getHealthStatus() { return healthStatus; } public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public String getVetName() { return vetName; } public void setVetName(String vetName) { this.vetName = vetName; }
    public LocalDate getTreatmentDate() { return treatmentDate; } public void setTreatmentDate(LocalDate treatmentDate) { this.treatmentDate = treatmentDate; }
    public LocalDate getFollowUpDate() { return followUpDate; } public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    public LocalDate getMilkWithdrawalUntilDate() { return milkWithdrawalUntilDate; } public void setMilkWithdrawalUntilDate(LocalDate milkWithdrawalUntilDate) { this.milkWithdrawalUntilDate = milkWithdrawalUntilDate; }
    public LocalDate getSlaughterWithdrawalUntilDate() { return slaughterWithdrawalUntilDate; } public void setSlaughterWithdrawalUntilDate(LocalDate slaughterWithdrawalUntilDate) { this.slaughterWithdrawalUntilDate = slaughterWithdrawalUntilDate; }
}
