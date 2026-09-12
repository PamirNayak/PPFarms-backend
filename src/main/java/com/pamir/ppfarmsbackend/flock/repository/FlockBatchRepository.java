package com.pamir.ppfarmsbackend.flock.repository;

import com.pamir.ppfarmsbackend.flock.entity.FlockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlockBatchRepository extends JpaRepository<FlockBatch, UUID> {
    List<FlockBatch> findByOrganizationIdAndDeletedAtIsNullOrderByArrivalDateDesc(UUID organizationId);
    Optional<FlockBatch> findByIdAndOrganizationIdAndDeletedAtIsNull(UUID id, UUID organizationId);
    List<FlockBatch> findByOrganizationIdAndStatusAndDeletedAtIsNull(UUID organizationId, String status);
    boolean existsByShedPenIdAndDeletedAtIsNull(UUID shedPenId);
}
