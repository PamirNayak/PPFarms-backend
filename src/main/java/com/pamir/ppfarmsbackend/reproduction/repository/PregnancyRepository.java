package com.pamir.ppfarmsbackend.reproduction.repository;

import com.pamir.ppfarmsbackend.reproduction.entity.Pregnancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PregnancyRepository extends JpaRepository<Pregnancy, UUID> {
    List<Pregnancy> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);
    List<Pregnancy> findByOrganizationIdAndStatus(UUID organizationId, String status);
    List<Pregnancy> findByExpectedDueDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
    long countByOrganizationIdAndStatus(UUID organizationId, String status);
}
