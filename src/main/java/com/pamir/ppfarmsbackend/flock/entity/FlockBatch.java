package com.pamir.ppfarmsbackend.flock.entity;

import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "flock_batches", indexes = {
    @Index(name = "idx_flock_org_status", columnList = "organization_id, status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlockBatch extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "batch_name", nullable = false, length = 100)
    private String batchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shed_pen_id")
    private ShedPen shedPen;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "initial_age_weeks")
    @Builder.Default
    private Integer initialAgeWeeks = 1;

    @Column(length = 30)
    @Builder.Default
    private String purpose = "DUAL"; // MEAT, EGGS, DUAL, BREEDING

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, SOLD, COMPLETED

    @Column(name = "purchase_cost", precision = 10, scale = 2)
    private BigDecimal purchaseCost;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
