package com.pamir.ppfarmsbackend.billing.repository;

import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByOrganizationId(UUID organizationId);
    List<Subscription> findByStatus(String status);
    long countByStatus(String status);
}

