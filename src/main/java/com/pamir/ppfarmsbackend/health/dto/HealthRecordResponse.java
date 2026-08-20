package com.pamir.ppfarmsbackend.health.dto;

import com.pamir.ppfarmsbackend.health.entity.HealthRecord;
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
public class HealthRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID animalId;
    private String symptoms;
    private String treatment;
    private String healthStatus;
    private String vetName;
    private LocalDate treatmentDate;
    private LocalDate followUpDate;
    private LocalDate milkWithdrawalUntilDate;
    private LocalDate slaughterWithdrawalUntilDate;
    private OffsetDateTime createdAt;

    public static HealthRecordResponse fromEntity(HealthRecord hr) {
        if (hr == null) return null;
        return HealthRecordResponse.builder()
                .id(hr.getId())
                .organizationId(hr.getOrganizationId())
                .animalId(hr.getAnimalId())
                .symptoms(hr.getSymptoms())
                .treatment(hr.getTreatment())
                .healthStatus(hr.getHealthStatus())
                .vetName(hr.getVetName())
                .treatmentDate(hr.getTreatmentDate())
                .followUpDate(hr.getFollowUpDate())
                .milkWithdrawalUntilDate(hr.getMilkWithdrawalUntilDate())
                .slaughterWithdrawalUntilDate(hr.getSlaughterWithdrawalUntilDate())
                .createdAt(hr.getCreatedAt())
                .build();
    }
}