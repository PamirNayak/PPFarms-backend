package com.pamir.ppfarmsbackend.herd.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class WeightRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.10", message = "Weight must be greater than 0")
    private BigDecimal weightKg;

    @NotNull(message = "Measurement date is required")
    private LocalDate measuredAt;

    private String notes;

    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public BigDecimal getWeightKg() { return weightKg; } public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public LocalDate getMeasuredAt() { return measuredAt; } public void setMeasuredAt(LocalDate measuredAt) { this.measuredAt = measuredAt; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }

    public static WeightRequestBuilder builder() { return new WeightRequestBuilder(); }
    public static class WeightRequestBuilder {
        private final WeightRequest req = new WeightRequest();
        public WeightRequestBuilder animalId(UUID animalId) { req.setAnimalId(animalId); return this; }
        public WeightRequestBuilder weightKg(BigDecimal weightKg) { req.setWeightKg(weightKg); return this; }
        public WeightRequestBuilder measuredAt(LocalDate measuredAt) { req.setMeasuredAt(measuredAt); return this; }
        public WeightRequestBuilder notes(String notes) { req.setNotes(notes); return this; }
        public WeightRequest build() { return req; }
    }
}
