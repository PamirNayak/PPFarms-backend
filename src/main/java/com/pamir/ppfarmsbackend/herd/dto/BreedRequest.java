package com.pamir.ppfarmsbackend.herd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BreedRequest {
    @NotNull(message = "Species ID is required")
    private UUID speciesId;

    @NotBlank(message = "Breed name is required (e.g. Beetal, Barbari, Holstein Friesian)")
    private String name;

    private String description;
}
