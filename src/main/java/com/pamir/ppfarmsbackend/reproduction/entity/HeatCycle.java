package com.pamir.ppfarmsbackend.reproduction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "heat_cycles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "observed_at", nullable = false)
    private LocalDate observedAt;

    @Column(name = "expected_next_heat", nullable = false)
    private LocalDate expectedNextHeat;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "OBSERVED"; // OBSERVED, SERVED_BRED, MISSED

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public LocalDate getObservedAt() { return observedAt; } public void setObservedAt(LocalDate observedAt) { this.observedAt = observedAt; }
    public LocalDate getExpectedNextHeat() { return expectedNextHeat; } public void setExpectedNextHeat(LocalDate expectedNextHeat) { this.expectedNextHeat = expectedNextHeat; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static HeatCycleBuilder builder() { return new HeatCycleBuilder(); }
    public static class HeatCycleBuilder {
        private final HeatCycle hc = new HeatCycle();
        public HeatCycleBuilder id(UUID id) { hc.setId(id); return this; }
        public HeatCycleBuilder organizationId(UUID organizationId) { hc.setOrganizationId(organizationId); return this; }
        public HeatCycleBuilder animalId(UUID animalId) { hc.setAnimalId(animalId); return this; }
        public HeatCycleBuilder observedAt(LocalDate observedAt) { hc.setObservedAt(observedAt); return this; }
        public HeatCycleBuilder expectedNextHeat(LocalDate expectedNextHeat) { hc.setExpectedNextHeat(expectedNextHeat); return this; }
        public HeatCycleBuilder status(String status) { hc.setStatus(status); return this; }
        public HeatCycleBuilder createdAt(OffsetDateTime createdAt) { hc.setCreatedAt(createdAt); return this; }
        public HeatCycle build() { return hc; }
    }
}
