package com.pamir.ppfarmsbackend.herd.repository;

import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShedPenRepository extends JpaRepository<ShedPen, UUID> {
    List<ShedPen> findByOrganizationIdAndDeletedAtIsNull(UUID organizationId);
}
