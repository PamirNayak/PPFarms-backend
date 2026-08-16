package com.pamir.ppfarmsbackend.reproduction.repository;

import com.pamir.ppfarmsbackend.reproduction.entity.BreedingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BreedingRepository extends JpaRepository<BreedingRecord, UUID> {
    List<BreedingRecord> findByOrganizationIdAndDamId(UUID organizationId, UUID damId);
    List<BreedingRecord> findByOrganizationIdAndDamIdOrderByBredAtDesc(UUID organizationId, UUID damId);
    List<BreedingRecord> findByOrganizationIdAndSireIdOrderByBredAtDesc(UUID organizationId, UUID sireId);
    List<BreedingRecord> findByOrganizationIdAndOutcome(UUID organizationId, String outcome);
}
