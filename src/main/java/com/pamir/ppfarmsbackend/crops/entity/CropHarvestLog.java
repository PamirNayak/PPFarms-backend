package com.pamir.ppfarmsbackend.crops.entity;

import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "crop_harvest_logs", indexes = {
    @Index(name = "idx_harvest_org_plot", columnList = "organization_id, crop_plot_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropHarvestLog extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_plot_id", nullable = false)
    private CropPlot cropPlot;

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @Column(name = "yield_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal yieldKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_feed_id")
    private FeedInventory destinationFeed;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
