package com.pamir.ppfarmsbackend.health.dto;

import com.pamir.ppfarmsbackend.health.entity.MortalityRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MortalityRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID animalId;
    private LocalDate deathDate;
    private String causeOfDeath;
    private String necropsyNotes;
    private String disposalMethod;
    private OffsetDateTime createdAt;

    public static MortalityRecordResponse fromEntity(MortalityRecord mr) {
        if (mr == null) return null;
        return MortalityRecordResponse.builder()
                .id(mr.getId())
                .organizationId(mr.getOrganizationId())
                .animalId(mr.getAnimalId())
                .deathDate(mr.getDeathDate())
                .causeOfDeath(mr.getCauseOfDeath())
                .necropsyNotes(mr.getNecropsyNotes())
                .disposalMethod(mr.getDisposalMethod())
                .createdAt(mr.getCreatedAt())
                .build();
    }
}