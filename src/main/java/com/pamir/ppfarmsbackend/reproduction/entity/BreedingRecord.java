package com.pamir.ppfarmsbackend.reproduction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "breeding_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreedingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "dam_id", nullable = false)
    private UUID damId;

    @Column(name = "sire_id", nullable = false)
    private UUID sireId;

    @Column(name = "breeding_type", nullable = false, length = 30)
    private String breedingType; // NATURAL, ARTIFICIAL_INSEMINATION

    @Column(name = "bred_at", nullable = false)
    private LocalDate bredAt;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String outcome = "PENDING_CONFIRMATION"; // PENDING_CONFIRMATION, PREGNANT, FAILED

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getDamId() { return damId; } public void setDamId(UUID damId) { this.damId = damId; }
    public UUID getSireId() { return sireId; } public void setSireId(UUID sireId) { this.sireId = sireId; }
    public String getBreedingType() { return breedingType; } public void setBreedingType(String breedingType) { this.breedingType = breedingType; }
    public LocalDate getBredAt() { return bredAt; } public void setBredAt(LocalDate bredAt) { this.bredAt = bredAt; }
    public String getOutcome() { return outcome; } public void setOutcome(String outcome) { this.outcome = outcome; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static BreedingRecordBuilder builder() { return new BreedingRecordBuilder(); }
    public static class BreedingRecordBuilder {
        private final BreedingRecord br = new BreedingRecord();
        public BreedingRecordBuilder id(UUID id) { br.setId(id); return this; }
        public BreedingRecordBuilder organizationId(UUID organizationId) { br.setOrganizationId(organizationId); return this; }
        public BreedingRecordBuilder damId(UUID damId) { br.setDamId(damId); return this; }
        public BreedingRecordBuilder sireId(UUID sireId) { br.setSireId(sireId); return this; }
        public BreedingRecordBuilder breedingType(String breedingType) { br.setBreedingType(breedingType); return this; }
        public BreedingRecordBuilder bredAt(LocalDate bredAt) { br.setBredAt(bredAt); return this; }
        public BreedingRecordBuilder outcome(String outcome) { br.setOutcome(outcome); return this; }
        public BreedingRecordBuilder createdAt(OffsetDateTime createdAt) { br.setCreatedAt(createdAt); return this; }
        public BreedingRecord build() { return br; }
    }
}
