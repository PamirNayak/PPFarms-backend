package com.pamir.ppfarmsbackend.accounting.repository;

import com.pamir.ppfarmsbackend.accounting.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByOrganizationIdOrderByExpenseDateDesc(UUID organizationId);

    Optional<Expense> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
