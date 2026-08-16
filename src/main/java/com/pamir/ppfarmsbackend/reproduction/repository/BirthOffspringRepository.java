package com.pamir.ppfarmsbackend.reproduction.repository;

import com.pamir.ppfarmsbackend.reproduction.entity.BirthOffspring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BirthOffspringRepository extends JpaRepository<BirthOffspring, UUID> {
    List<BirthOffspring> findByBirthRecordId(UUID birthRecordId);
}
