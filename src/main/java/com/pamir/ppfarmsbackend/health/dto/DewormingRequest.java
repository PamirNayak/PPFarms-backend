package com.pamir.ppfarmsbackend.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DewormingRequest {

    private List<UUID> animalIds;
    private UUID shedPenId;

    @NotBlank(message = "Drug name is required")
    private String drugName;

    private String drugType; // BENZIMIDAZOLE, MACROCYCLIC_LACTONE, LEVAMISOLE, COMBINATION

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotNull(message = "Administered date is required")
    private LocalDate administeredAt;

    @NotNull(message = "Next due date is required")
    private LocalDate nextDueDate;
}
