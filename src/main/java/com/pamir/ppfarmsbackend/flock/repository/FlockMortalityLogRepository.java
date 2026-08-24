package com.pamir.ppfarmsbackend.flock.repository;

import com.pamir.ppfarmsbackend.flock.entity.FlockMortalityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlockMortalityLogRepository extends JpaRepository<FlockMortalityLog, UUID> {
    List<FlockMortalityLog> findByOrganizationIdAndFlockBatchIdOrderByLogDateDesc(UUID organizationId, UUID flockBatchId);
    List<FlockMortalityLog> findByOrganizationIdOrderByLogDateDesc(UUID organizationId);
}
