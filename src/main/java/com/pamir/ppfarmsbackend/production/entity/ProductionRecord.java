package com.pamir.ppfarmsbackend.production.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "production_records", indexes = {
    @Index(name = "idx_production_org_date", columnList = "organization_id, recorded_date")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionRecord extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "animal_id")
    private UUID animalId;

    @Column(name = "shed_pen_id")
    private UUID shedPenId;

    @Column(name = "production_type", nullable = false, length = 50)
    private String productionType;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "fat_percentage", precision = 5, scale = 2)
    private BigDecimal fatPercentage;

    @Column(name = "snf_percentage", precision = 5, scale = 2)
    private BigDecimal snfPercentage;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}