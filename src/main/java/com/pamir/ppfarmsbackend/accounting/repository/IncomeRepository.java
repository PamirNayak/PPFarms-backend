package com.pamir.ppfarmsbackend.accounting.repository;

import com.pamir.ppfarmsbackend.accounting.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {

    List<Income> findByOrganizationIdOrderByIncomeDateDesc(UUID organizationId);

    Optional<Income> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
