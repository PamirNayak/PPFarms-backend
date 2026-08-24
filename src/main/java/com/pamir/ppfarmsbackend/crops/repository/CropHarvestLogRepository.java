package com.pamir.ppfarmsbackend.crops.repository;

import com.pamir.ppfarmsbackend.crops.entity.CropHarvestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CropHarvestLogRepository extends JpaRepository<CropHarvestLog, UUID> {
    List<CropHarvestLog> findByOrganizationIdOrderByHarvestDateDesc(UUID organizationId);
    List<CropHarvestLog> findByOrganizationIdAndCropPlotIdOrderByHarvestDateDesc(UUID organizationId, UUID cropPlotId);
}
