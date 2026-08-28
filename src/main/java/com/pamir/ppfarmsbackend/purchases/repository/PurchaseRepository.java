package com.pamir.ppfarmsbackend.purchases.repository;

import com.pamir.ppfarmsbackend.purchases.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    List<Purchase> findByOrganizationIdAndDeletedAtIsNull(UUID organizationId);
    List<Purchase> findByOrganizationIdAndPurchaseDateBetweenAndDeletedAtIsNull(UUID organizationId, LocalDate startDate, LocalDate endDate);
}
