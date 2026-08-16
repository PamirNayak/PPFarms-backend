package com.pamir.ppfarmsbackend.reproduction.dto;

import com.pamir.ppfarmsbackend.reproduction.entity.Pregnancy;
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
public class PregnancyResponse {
    private UUID id;
    private UUID organizationId;
    private UUID breedingRecordId;
    private UUID animalId;
    private String status;
    private LocalDate confirmationDate;
    private LocalDate expectedDueDate;
    private LocalDate actualDeliveryDate;
    private OffsetDateTime createdAt;

    public static PregnancyResponse fromEntity(Pregnancy p) {
        if (p == null) return null;
        return PregnancyResponse.builder()
                .id(p.getId())
                .organizationId(p.getOrganizationId())
                .breedingRecordId(p.getBreedingRecordId())
                .animalId(p.getAnimalId())
                .status(p.getStatus())
                .confirmationDate(p.getConfirmationDate())
                .expectedDueDate(p.getExpectedDueDate())
                .actualDeliveryDate(p.getActualDeliveryDate())
                .createdAt(p.getCreatedAt())
                .build();
    }
}