package com.pamir.ppfarmsbackend.billing.repository;

import com.pamir.ppfarmsbackend.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByOrganizationId(UUID organizationId);
}
