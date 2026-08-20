package com.pamir.ppfarmsbackend.health.repository;

import com.pamir.ppfarmsbackend.health.entity.MortalityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MortalityRepository extends JpaRepository<MortalityRecord, UUID> {
    List<MortalityRecord> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);
    List<MortalityRecord> findByOrganizationId(UUID organizationId);
}
