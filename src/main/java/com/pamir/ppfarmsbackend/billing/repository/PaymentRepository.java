package com.pamir.ppfarmsbackend.billing.repository;

import com.pamir.ppfarmsbackend.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByStatus(String status);
    List<Payment> findByStatusOrderByCreatedAtDesc(String status);
    List<Payment> findByOrganizationId(UUID organizationId);
    List<Payment> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<Payment> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'APPROVED'")
    BigDecimal sumApprovedRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'APPROVED' AND p.createdAt >= :startDate")
    BigDecimal sumApprovedRevenueSince(@Param("startDate") OffsetDateTime startDate);

    long countByStatus(String status);
}

