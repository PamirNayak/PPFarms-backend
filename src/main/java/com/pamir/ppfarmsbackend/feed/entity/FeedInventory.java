package com.pamir.ppfarmsbackend.feed.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "feed_inventory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedInventory extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "feed_name", nullable = false, length = 100)
    private String feedName;

    @Column(name = "feed_category", nullable = false, length = 50)
    private String feedCategory;

    @Column(name = "quantity_kg", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal quantityKg = BigDecimal.ZERO;

    @Column(name = "min_threshold_kg", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal minThresholdKg = BigDecimal.ZERO;

    @Column(name = "cost_per_kg", precision = 10, scale = 2)
    private BigDecimal costPerKg;
}