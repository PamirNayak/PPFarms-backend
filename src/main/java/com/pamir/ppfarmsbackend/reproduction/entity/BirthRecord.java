package com.pamir.ppfarmsbackend.reproduction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "birth_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BirthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "pregnancy_id", nullable = false)
    private UUID pregnancyId;

    @Column(name = "dam_id", nullable = false)
    private UUID damId;

    @Column(name = "sire_id", nullable = false)
    private UUID sireId;

    @Column(name = "total_born", nullable = false)
    private Integer totalBorn;

    @Column(name = "alive_count", nullable = false)
    private Integer aliveCount;

    @Column(name = "stillborn_count", nullable = false)
    @Builder.Default
    private Integer stillbornCount = 0;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getPregnancyId() { return pregnancyId; } public void setPregnancyId(UUID pregnancyId) { this.pregnancyId = pregnancyId; }
    public UUID getDamId() { return damId; } public void setDamId(UUID damId) { this.damId = damId; }
    public UUID getSireId() { return sireId; } public void setSireId(UUID sireId) { this.sireId = sireId; }
    public Integer getTotalBorn() { return totalBorn; } public void setTotalBorn(Integer totalBorn) { this.totalBorn = totalBorn; }
    public Integer getAliveCount() { return aliveCount; } public void setAliveCount(Integer aliveCount) { this.aliveCount = aliveCount; }
    public Integer getStillbornCount() { return stillbornCount; } public void setStillbornCount(Integer stillbornCount) { this.stillbornCount = stillbornCount; }
    public LocalDate getBirthDate() { return birthDate; } public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getDeliveryNotes() { return deliveryNotes; } public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static BirthRecordBuilder builder() { return new BirthRecordBuilder(); }
    public static class BirthRecordBuilder {
        private final BirthRecord br = new BirthRecord();
        public BirthRecordBuilder id(UUID id) { br.setId(id); return this; }
        public BirthRecordBuilder organizationId(UUID organizationId) { br.setOrganizationId(organizationId); return this; }
        public BirthRecordBuilder pregnancyId(UUID pregnancyId) { br.setPregnancyId(pregnancyId); return this; }
        public BirthRecordBuilder damId(UUID damId) { br.setDamId(damId); return this; }
        public BirthRecordBuilder sireId(UUID sireId) { br.setSireId(sireId); return this; }
        public BirthRecordBuilder totalBorn(Integer totalBorn) { br.setTotalBorn(totalBorn); return this; }
        public BirthRecordBuilder aliveCount(Integer aliveCount) { br.setAliveCount(aliveCount); return this; }
        public BirthRecordBuilder stillbornCount(Integer stillbornCount) { br.setStillbornCount(stillbornCount); return this; }
        public BirthRecordBuilder birthDate(LocalDate birthDate) { br.setBirthDate(birthDate); return this; }
        public BirthRecordBuilder deliveryNotes(String deliveryNotes) { br.setDeliveryNotes(deliveryNotes); return this; }
        public BirthRecordBuilder createdAt(OffsetDateTime createdAt) { br.setCreatedAt(createdAt); return this; }
        public BirthRecord build() { return br; }
    }
}
