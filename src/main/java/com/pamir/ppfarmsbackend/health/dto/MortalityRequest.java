package com.pamir.ppfarmsbackend.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MortalityRequest {

    @NotNull(message = "Animal ID is required")
    private UUID animalId;

    @NotNull(message = "Death date is required")
    private LocalDate deathDate;

    @NotBlank(message = "Cause of death is required")
    private String causeOfDeath;

    private String necropsyNotes;

    @NotBlank(message = "Disposal method is required")
    private String disposalMethod; // BURIAL, INCINERATION, RENDERING, DEEP_BURIAL_WITH_LIME
}
