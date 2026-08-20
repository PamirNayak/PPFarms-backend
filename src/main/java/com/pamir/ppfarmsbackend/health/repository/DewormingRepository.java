package com.pamir.ppfarmsbackend.health.repository;

import com.pamir.ppfarmsbackend.health.entity.DewormingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DewormingRepository extends JpaRepository<DewormingRecord, UUID> {
    List<DewormingRecord> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);
    List<DewormingRecord> findByOrganizationId(UUID organizationId);
    List<DewormingRecord> findByOrganizationIdAndNextDueDateBetween(UUID organizationId, LocalDate start, LocalDate end);
}
