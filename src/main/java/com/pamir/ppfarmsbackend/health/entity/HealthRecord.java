package com.pamir.ppfarmsbackend.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_records", indexes = {
    @Index(name = "idx_health_org_animal", columnList = "organization_id, animal_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String treatment;

    @Column(name = "health_status", nullable = false, length = 30)
    private String healthStatus;

    @Column(name = "vet_name", length = 100)
    private String vetName;

    @Column(name = "treatment_date", nullable = false)
    private LocalDate treatmentDate;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "milk_withdrawal_until_date")
    private LocalDate milkWithdrawalUntilDate;

    @Column(name = "slaughter_withdrawal_until_date")
    private LocalDate slaughterWithdrawalUntilDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}