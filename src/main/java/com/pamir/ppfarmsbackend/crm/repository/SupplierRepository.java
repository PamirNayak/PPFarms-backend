package com.pamir.ppfarmsbackend.crm.repository;

import com.pamir.ppfarmsbackend.crm.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findByOrganizationIdAndDeletedAtIsNullOrderByNameAsc(UUID organizationId);
}
