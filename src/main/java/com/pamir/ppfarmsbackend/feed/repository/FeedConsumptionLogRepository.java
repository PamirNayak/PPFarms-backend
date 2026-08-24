package com.pamir.ppfarmsbackend.feed.repository;

import com.pamir.ppfarmsbackend.feed.entity.FeedConsumptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FeedConsumptionLogRepository extends JpaRepository<FeedConsumptionLog, UUID> {
    List<FeedConsumptionLog> findByOrganizationIdAndConsumedDateBetween(UUID organizationId, LocalDate startDate, LocalDate endDate);
}
