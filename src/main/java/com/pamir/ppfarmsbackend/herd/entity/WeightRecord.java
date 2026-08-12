package com.pamir.ppfarmsbackend.herd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "weight_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "weight_kg", nullable = false, precision = 7, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "measured_at", nullable = false)
    private LocalDate measuredAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public BigDecimal getWeightKg() { return weightKg; } public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public LocalDate getMeasuredAt() { return measuredAt; } public void setMeasuredAt(LocalDate measuredAt) { this.measuredAt = measuredAt; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static WeightRecordBuilder builder() { return new WeightRecordBuilder(); }
    public static class WeightRecordBuilder {
        private final WeightRecord rec = new WeightRecord();
        public WeightRecordBuilder id(UUID id) { rec.setId(id); return this; }
        public WeightRecordBuilder organizationId(UUID organizationId) { rec.setOrganizationId(organizationId); return this; }
        public WeightRecordBuilder animalId(UUID animalId) { rec.setAnimalId(animalId); return this; }
        public WeightRecordBuilder weightKg(BigDecimal weightKg) { rec.setWeightKg(weightKg); return this; }
        public WeightRecordBuilder measuredAt(LocalDate measuredAt) { rec.setMeasuredAt(measuredAt); return this; }
        public WeightRecordBuilder notes(String notes) { rec.setNotes(notes); return this; }
        public WeightRecordBuilder createdAt(OffsetDateTime createdAt) { rec.setCreatedAt(createdAt); return this; }
        public WeightRecord build() { return rec; }
    }
}
