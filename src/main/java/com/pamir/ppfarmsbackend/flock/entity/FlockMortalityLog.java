package com.pamir.ppfarmsbackend.flock.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "flock_mortality_logs", indexes = {
    @Index(name = "idx_mortality_org_batch", columnList = "organization_id, flock_batch_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlockMortalityLog extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_batch_id", nullable = false)
    private FlockBatch flockBatch;

    @Column(name = "dead_count", nullable = false)
    private Integer deadCount;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "cause_of_death", length = 100)
    private String causeOfDeath;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
