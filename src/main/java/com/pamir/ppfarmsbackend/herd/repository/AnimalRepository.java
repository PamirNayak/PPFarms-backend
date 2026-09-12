package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID>, JpaSpecificationExecutor<Animal> {
    Optional<Animal> findByIdAndOrganizationIdAndDeletedAtIsNull(UUID id, UUID organizationId);
    Optional<Animal> findByOrganizationIdAndTagNumberAndDeletedAtIsNull(UUID organizationId, String tagNumber);
    boolean existsByOrganizationIdAndTagNumberAndDeletedAtIsNull(UUID organizationId, String tagNumber);
    long countByOrganizationIdAndStatusAndDeletedAtIsNull(UUID organizationId, String status);
    long countByOrganizationIdAndDeletedAtIsNull(UUID organizationId);
    long countByDeletedAtIsNull();
    boolean existsBySpeciesIdAndDeletedAtIsNull(UUID speciesId);
    boolean existsByBreedIdAndDeletedAtIsNull(UUID breedId);
    boolean existsByShedPenIdAndDeletedAtIsNull(UUID shedPenId);
}

