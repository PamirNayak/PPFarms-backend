package com.pamir.ppfarmsbackend.health.repository;

import com.pamir.ppfarmsbackend.health.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HealthRepository extends JpaRepository<HealthRecord, UUID> {
    List<HealthRecord> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);

    @Query("SELECT h FROM HealthRecord h WHERE h.organizationId = :organizationId AND (:animalId IS NULL OR h.animalId = :animalId) ORDER BY h.treatmentDate DESC")
    List<HealthRecord> findByOrganizationIdAndOptionalAnimalId(@Param("organizationId") UUID organizationId, @Param("animalId") UUID animalId);
}
