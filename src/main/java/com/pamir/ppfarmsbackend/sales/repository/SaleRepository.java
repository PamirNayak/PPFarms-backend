package com.pamir.ppfarmsbackend.sales.repository;

import com.pamir.ppfarmsbackend.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    List<Sale> findByOrganizationIdOrderBySaleDateDesc(UUID organizationId);

    Optional<Sale> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByInvoiceNumber(String invoiceNumber);
}
