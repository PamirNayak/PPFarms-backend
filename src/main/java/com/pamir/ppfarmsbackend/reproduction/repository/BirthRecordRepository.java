package com.pamir.ppfarmsbackend.reproduction.repository;

import com.pamir.ppfarmsbackend.reproduction.entity.BirthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BirthRecordRepository extends JpaRepository<BirthRecord, UUID> {
    List<BirthRecord> findByOrganizationIdAndDamId(UUID organizationId, UUID damId);
}
