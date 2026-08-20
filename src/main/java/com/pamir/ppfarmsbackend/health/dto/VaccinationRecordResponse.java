package com.pamir.ppfarmsbackend.health.dto;

import com.pamir.ppfarmsbackend.health.entity.VaccinationRecord;
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
public class VaccinationRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID animalId;
    private String vaccineName;
    private String batchNumber;
    private String dosage;
    private String status;
    private LocalDate administeredAt;
    private LocalDate nextDueDate;
    private OffsetDateTime createdAt;

    public static VaccinationRecordResponse fromEntity(VaccinationRecord vr) {
        if (vr == null) return null;
        return VaccinationRecordResponse.builder()
                .id(vr.getId())
                .organizationId(vr.getOrganizationId())
                .animalId(vr.getAnimalId())
                .vaccineName(vr.getVaccineName())
                .batchNumber(vr.getBatchNumber())
                .dosage(vr.getDosage())
                .status(vr.getStatus())
                .administeredAt(vr.getAdministeredAt())
                .nextDueDate(vr.getNextDueDate())
                .createdAt(vr.getCreatedAt())
                .build();
    }
}