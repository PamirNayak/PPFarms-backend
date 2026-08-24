package com.pamir.ppfarmsbackend.flock.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "egg_production_logs", indexes = {
    @Index(name = "idx_eggs_org_batch", columnList = "organization_id, flock_batch_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EggProductionLog extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_batch_id", nullable = false)
    private FlockBatch flockBatch;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "total_eggs", nullable = false)
    private Integer totalEggs;

    @Column(name = "broken_eggs")
    @Builder.Default
    private Integer brokenEggs = 0;

    @Column(name = "trays_count")
    private Integer traysCount;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
