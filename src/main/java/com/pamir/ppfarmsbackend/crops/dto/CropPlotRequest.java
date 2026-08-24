package com.pamir.ppfarmsbackend.crops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPlotRequest {

    @NotBlank(message = "Plot name is required")
    private String plotName;

    @NotNull(message = "Area in acres is required")
    private BigDecimal areaAcres;

    @NotBlank(message = "Crop name is required")
    private String cropName; // NAPIER, MAIZE, LUCERNE, SORGHUM, HYBRID_GRASS, SILAGE

    private String variety;

    @NotNull(message = "Sowing date is required")
    private LocalDate sowingDate;

    @NotNull(message = "Expected harvest date is required")
    private LocalDate expectedHarvestDate;

    @Builder.Default
    private String status = "GROWING"; // SOWN, GROWING, READY_FOR_HARVEST, HARVESTED, FALLOW

    @Builder.Default
    private String irrigationType = "DRIP"; // DRIP, SPRINKLER, FLOOD, RAIN_FED

    private BigDecimal productionCost;
    private String notes;
}
