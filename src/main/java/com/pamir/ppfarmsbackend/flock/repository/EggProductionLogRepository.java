package com.pamir.ppfarmsbackend.flock.repository;

import com.pamir.ppfarmsbackend.flock.entity.EggProductionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EggProductionLogRepository extends JpaRepository<EggProductionLog, UUID> {
    List<EggProductionLog> findByOrganizationIdAndFlockBatchIdOrderByCollectionDateDesc(UUID organizationId, UUID flockBatchId);
    List<EggProductionLog> findByOrganizationIdOrderByCollectionDateDesc(UUID organizationId);
}
