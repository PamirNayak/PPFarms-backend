package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeightRepository extends JpaRepository<WeightRecord, UUID> {
    List<WeightRecord> findByOrganizationIdAndAnimalIdOrderByMeasuredAtDesc(UUID organizationId, UUID animalId);
    Optional<WeightRecord> findFirstByOrganizationIdAndAnimalIdOrderByMeasuredAtDesc(UUID organizationId, UUID animalId);
}
