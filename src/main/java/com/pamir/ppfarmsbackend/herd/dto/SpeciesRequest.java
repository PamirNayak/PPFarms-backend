package com.pamir.ppfarmsbackend.herd.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpeciesRequest {
    @NotBlank(message = "Species name is required (e.g. Goat, Sheep, Cattle)")
    private String name;

    private Integer gestationDays;
}
