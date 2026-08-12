package com.pamir.ppfarmsbackend.herd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimalResponse {

    private UUID id;
    private UUID organizationId;
    private UUID speciesId;
    private String speciesName;
    private UUID breedId;
    private String breedName;
    private UUID shedPenId;
    private String shedPenName;
    private String tagNumber;
    private String name;
    private String gender;
    private String status;
    private LocalDate dateOfBirth;
    private BigDecimal birthWeight;
    private BigDecimal currentWeight;
    private UUID sireId;
    private String sireTagNumber;
    private UUID damId;
    private String damTagNumber;
    private String color;
    private BigDecimal height;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private String source;
    private String photoUrl;
    private Double averageDailyGainGrams;
    private String growthPerformance; // FAST_GROWTH, NORMAL, STUNTED
    private OffsetDateTime createdAt;
}
