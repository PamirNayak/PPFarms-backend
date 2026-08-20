package com.pamir.ppfarmsbackend.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deworming_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DewormingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(name = "drug_name", nullable = false, length = 100)
    private String drugName;

    @Column(name = "drug_type", nullable = false, length = 50)
    private String drugType;

    @Column(nullable = false, length = 50)
    private String dosage;

    @Column(name = "administered_at", nullable = false)
    private LocalDate administeredAt;

    @Column(name = "next_due_date", nullable = false)
    private LocalDate nextDueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}