package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BreedRepository extends JpaRepository<Breed, UUID> {
    @Query("SELECT b FROM Breed b WHERE (:speciesId IS NULL OR b.species.id = :speciesId) AND (b.organizationId IS NULL OR b.organizationId = :organizationId)")
    List<Breed> findBySpeciesAndOrganization(@Param("speciesId") UUID speciesId, @Param("organizationId") UUID organizationId);

    boolean existsBySpeciesId(UUID speciesId);
}
