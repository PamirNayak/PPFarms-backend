package com.pamir.ppfarmsbackend.crops.repository;

import com.pamir.ppfarmsbackend.crops.entity.CropPlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CropPlotRepository extends JpaRepository<CropPlot, UUID> {
    List<CropPlot> findByOrganizationIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID organizationId);
    List<CropPlot> findByOrganizationIdAndStatusAndDeletedAtIsNull(UUID organizationId, String status);
}
