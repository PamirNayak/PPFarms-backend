package com.pamir.ppfarmsbackend.production.repository;

import com.pamir.ppfarmsbackend.production.entity.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, UUID> {
    List<ProductionRecord> findByOrganizationIdAndRecordedDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
    List<ProductionRecord> findByOrganizationIdAndAnimalId(UUID organizationId, UUID animalId);

    @Query("SELECT SUM(p.quantity) FROM ProductionRecord p WHERE p.organizationId = :organizationId AND p.productionType = :productionType AND p.recordedDate BETWEEN :startDate AND :endDate")
    BigDecimal sumQuantityByPeriod(@Param("organizationId") UUID organizationId, @Param("productionType") String productionType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
