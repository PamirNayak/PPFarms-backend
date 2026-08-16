package com.pamir.ppfarmsbackend.reproduction.dto;

import com.pamir.ppfarmsbackend.reproduction.entity.BreedingRecord;
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
public class BreedingRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID damId;
    private UUID sireId;
    private String breedingType;
    private LocalDate bredAt;
    private String outcome;
    private OffsetDateTime createdAt;

    public static BreedingRecordResponse fromEntity(BreedingRecord br) {
        if (br == null) return null;
        return BreedingRecordResponse.builder()
                .id(br.getId())
                .organizationId(br.getOrganizationId())
                .damId(br.getDamId())
                .sireId(br.getSireId())
                .breedingType(br.getBreedingType())
                .bredAt(br.getBredAt())
                .outcome(br.getOutcome())
                .createdAt(br.getCreatedAt())
                .build();
    }
}