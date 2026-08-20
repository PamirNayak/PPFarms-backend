package com.pamir.ppfarmsbackend.health.dto;

import com.pamir.ppfarmsbackend.health.entity.DewormingRecord;
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
public class DewormingRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID animalId;
    private String drugName;
    private String drugType;
    private String dosage;
    private LocalDate administeredAt;
    private LocalDate nextDueDate;
    private OffsetDateTime createdAt;

    public static DewormingRecordResponse fromEntity(DewormingRecord dr) {
        if (dr == null) return null;
        return DewormingRecordResponse.builder()
                .id(dr.getId())
                .organizationId(dr.getOrganizationId())
                .animalId(dr.getAnimalId())
                .drugName(dr.getDrugName())
                .drugType(dr.getDrugType())
                .dosage(dr.getDosage())
                .administeredAt(dr.getAdministeredAt())
                .nextDueDate(dr.getNextDueDate())
                .createdAt(dr.getCreatedAt())
                .build();
    }
}