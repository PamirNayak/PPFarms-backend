package com.pamir.ppfarmsbackend.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchVaccinationRequest {

    private UUID shedPenId;
    private List<UUID> animalIds;

    @NotBlank(message = "Vaccine name is required")
    private String vaccineName;

    private String batchNumber;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotNull(message = "Administration date is required")
    private LocalDate administeredAt;

    @NotNull(message = "Next due date is required")
    private LocalDate nextDueDate;

    public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
    public List<UUID> getAnimalIds() { return animalIds; } public void setAnimalIds(List<UUID> animalIds) { this.animalIds = animalIds; }
    public String getVaccineName() { return vaccineName; } public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public String getBatchNumber() { return batchNumber; } public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getDosage() { return dosage; } public void setDosage(String dosage) { this.dosage = dosage; }
    public LocalDate getAdministeredAt() { return administeredAt; } public void setAdministeredAt(LocalDate administeredAt) { this.administeredAt = administeredAt; }
    public LocalDate getNextDueDate() { return nextDueDate; } public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public static BatchVaccinationRequestBuilder builder() { return new BatchVaccinationRequestBuilder(); }
    public static class BatchVaccinationRequestBuilder {
        private final BatchVaccinationRequest bvr = new BatchVaccinationRequest();
        public BatchVaccinationRequestBuilder shedPenId(UUID shedPenId) { bvr.setShedPenId(shedPenId); return this; }
        public BatchVaccinationRequestBuilder animalIds(List<UUID> animalIds) { bvr.setAnimalIds(animalIds); return this; }
        public BatchVaccinationRequestBuilder vaccineName(String vaccineName) { bvr.setVaccineName(vaccineName); return this; }
        public BatchVaccinationRequestBuilder batchNumber(String batchNumber) { bvr.setBatchNumber(batchNumber); return this; }
        public BatchVaccinationRequestBuilder dosage(String dosage) { bvr.setDosage(dosage); return this; }
        public BatchVaccinationRequestBuilder administeredAt(LocalDate administeredAt) { bvr.setAdministeredAt(administeredAt); return this; }
        public BatchVaccinationRequestBuilder nextDueDate(LocalDate nextDueDate) { bvr.setNextDueDate(nextDueDate); return this; }
        public BatchVaccinationRequest build() { return bvr; }
    }
}
