package com.pamir.ppfarmsbackend.feed.repository;

import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedInventoryRepository extends JpaRepository<FeedInventory, UUID> {

    List<FeedInventory> findByOrganizationIdAndDeletedAtIsNull(UUID organizationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FeedInventory f WHERE f.id = :id AND f.organizationId = :organizationId")
    Optional<FeedInventory> findByIdAndOrganizationIdForUpdate(@Param("id") UUID id, @Param("organizationId") UUID organizationId);

    @Query("SELECT f FROM FeedInventory f WHERE f.organizationId = :organizationId AND f.quantityKg <= f.minThresholdKg AND f.deletedAt IS NULL")
    List<FeedInventory> findLowStockItems(@Param("organizationId") UUID organizationId);
}
