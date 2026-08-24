package com.pamir.ppfarmsbackend.crops.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "crop_plots", indexes = {
    @Index(name = "idx_crops_org_status", columnList = "organization_id, status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropPlot extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "plot_name", nullable = false, length = 100)
    private String plotName;

    @Column(name = "area_acres", nullable = false, precision = 6, scale = 2)
    private BigDecimal areaAcres;

    @Column(name = "crop_name", nullable = false, length = 100)
    private String cropName; // NAPIER, MAIZE, LUCERNE, SORGHUM, HYBRID_GRASS, SILAGE

    @Column(length = 100)
    private String variety;

    @Column(name = "sowing_date", nullable = false)
    private LocalDate sowingDate;

    @Column(name = "expected_harvest_date", nullable = false)
    private LocalDate expectedHarvestDate;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "GROWING"; // SOWN, GROWING, READY_FOR_HARVEST, HARVESTED, FALLOW

    @Column(name = "irrigation_type", length = 50)
    @Builder.Default
    private String irrigationType = "DRIP"; // DRIP, SPRINKLER, FLOOD, RAIN_FED

    @Column(name = "production_cost", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal productionCost = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
