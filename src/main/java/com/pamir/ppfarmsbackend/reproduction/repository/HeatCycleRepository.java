package com.pamir.ppfarmsbackend.reproduction.repository;

import com.pamir.ppfarmsbackend.reproduction.entity.HeatCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeatCycleRepository extends JpaRepository<HeatCycle, UUID> {
    List<HeatCycle> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);
}
