package com.pamir.ppfarmsbackend.health.repository;

import com.pamir.ppfarmsbackend.health.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VaccinationRepository extends JpaRepository<VaccinationRecord, UUID> {
    List<VaccinationRecord> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);
    List<VaccinationRecord> findByOrganizationId(UUID organizationId);
    List<VaccinationRecord> findByOrganizationIdAndNextDueDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
    List<VaccinationRecord> findByNextDueDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
}
