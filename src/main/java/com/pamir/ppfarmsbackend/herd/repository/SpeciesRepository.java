package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, UUID> {
    Optional<Species> findByNameIgnoreCase(String name);
}
