package com.pamir.ppfarmsbackend.herd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShedRequest {
    @NotBlank(message = "Shed/Pen name is required")
    private String name;

    @NotBlank(message = "Pen type is required (e.g. LACTATION, MATERNITY, QUARANTINE, GENERAL)")
    private String penType;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}
