package com.pamir.ppfarmsbackend.reproduction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pregnancies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pregnancy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "breeding_record_id", nullable = false)
    private UUID breedingRecordId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "CONFIRMED"; // CONFIRMED, DELIVERED, ABORTED

    @Column(name = "confirmation_date", nullable = false)
    private LocalDate confirmationDate;

    @Column(name = "expected_due_date", nullable = false)
    private LocalDate expectedDueDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getBreedingRecordId() { return breedingRecordId; } public void setBreedingRecordId(UUID breedingRecordId) { this.breedingRecordId = breedingRecordId; }
    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public LocalDate getConfirmationDate() { return confirmationDate; } public void setConfirmationDate(LocalDate confirmationDate) { this.confirmationDate = confirmationDate; }
    public LocalDate getExpectedDueDate() { return expectedDueDate; } public void setExpectedDueDate(LocalDate expectedDueDate) { this.expectedDueDate = expectedDueDate; }
    public LocalDate getActualDeliveryDate() { return actualDeliveryDate; } public void setActualDeliveryDate(LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static PregnancyBuilder builder() { return new PregnancyBuilder(); }
    public static class PregnancyBuilder {
        private final Pregnancy p = new Pregnancy();
        public PregnancyBuilder id(UUID id) { p.setId(id); return this; }
        public PregnancyBuilder organizationId(UUID organizationId) { p.setOrganizationId(organizationId); return this; }
        public PregnancyBuilder breedingRecordId(UUID breedingRecordId) { p.setBreedingRecordId(breedingRecordId); return this; }
        public PregnancyBuilder animalId(UUID animalId) { p.setAnimalId(animalId); return this; }
        public PregnancyBuilder status(String status) { p.setStatus(status); return this; }
        public PregnancyBuilder confirmationDate(LocalDate confirmationDate) { p.setConfirmationDate(confirmationDate); return this; }
        public PregnancyBuilder expectedDueDate(LocalDate expectedDueDate) { p.setExpectedDueDate(expectedDueDate); return this; }
        public PregnancyBuilder actualDeliveryDate(LocalDate actualDeliveryDate) { p.setActualDeliveryDate(actualDeliveryDate); return this; }
        public PregnancyBuilder createdAt(OffsetDateTime createdAt) { p.setCreatedAt(createdAt); return this; }
        public Pregnancy build() { return p; }
    }
}
