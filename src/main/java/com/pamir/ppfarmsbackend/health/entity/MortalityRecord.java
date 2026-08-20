package com.pamir.ppfarmsbackend.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mortality_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MortalityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "death_date", nullable = false)
    private LocalDate deathDate;

    @Column(name = "cause_of_death", nullable = false, length = 150)
    private String causeOfDeath;

    @Column(name = "necropsy_notes", columnDefinition = "TEXT")
    private String necropsyNotes;

    @Column(name = "disposal_method", nullable = false, length = 50)
    private String disposalMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}