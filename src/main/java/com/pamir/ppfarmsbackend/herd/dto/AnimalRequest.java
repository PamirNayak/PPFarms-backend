package com.pamir.ppfarmsbackend.herd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalRequest {

    @NotNull(message = "Species ID is required")
    private UUID speciesId;

    @NotNull(message = "Breed ID is required")
    private UUID breedId;

    private UUID shedPenId;

    @NotBlank(message = "Tag number is required")
    private String tagNumber;

    private String name;

    @NotBlank(message = "Gender is required")
    private String gender; // MALE, FEMALE, CASTRATED

    @Builder.Default
    private String status = "ACTIVE";
    private LocalDate dateOfBirth;
    private BigDecimal birthWeight;

    private UUID sireId; // Pedigree Father
    private UUID damId;  // Pedigree Mother

    private String color;
    private BigDecimal height;
    private BigDecimal purchasePrice;
    private LocalDate purchaseDate;
    private String source;

    private String photoUrl;
}
