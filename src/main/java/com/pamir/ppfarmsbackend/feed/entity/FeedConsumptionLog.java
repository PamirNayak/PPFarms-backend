package com.pamir.ppfarmsbackend.feed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "feed_consumption_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedConsumptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "feed_inventory_id", nullable = false)
    private UUID feedInventoryId;

    @Column(name = "shed_pen_id")
    private UUID shedPenId;

    @Column(name = "quantity_consumed_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityConsumedKg;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "consumed_date", nullable = false)
    private LocalDate consumedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}