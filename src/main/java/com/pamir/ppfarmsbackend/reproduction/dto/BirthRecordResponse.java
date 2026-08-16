package com.pamir.ppfarmsbackend.reproduction.dto;

import com.pamir.ppfarmsbackend.reproduction.entity.BirthRecord;
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
public class BirthRecordResponse {
    private UUID id;
    private UUID organizationId;
    private UUID pregnancyId;
    private UUID damId;
    private UUID sireId;
    private Integer totalBorn;
    private Integer aliveCount;
    private Integer stillbornCount;
    private LocalDate birthDate;
    private String deliveryNotes;
    private OffsetDateTime createdAt;

    public static BirthRecordResponse fromEntity(BirthRecord br) {
        if (br == null) return null;
        return BirthRecordResponse.builder()
                .id(br.getId())
                .organizationId(br.getOrganizationId())
                .pregnancyId(br.getPregnancyId())
                .damId(br.getDamId())
                .sireId(br.getSireId())
                .totalBorn(br.getTotalBorn())
                .aliveCount(br.getAliveCount())
                .stillbornCount(br.getStillbornCount())
                .birthDate(br.getBirthDate())
                .deliveryNotes(br.getDeliveryNotes())
                .createdAt(br.getCreatedAt())
                .build();
    }
}