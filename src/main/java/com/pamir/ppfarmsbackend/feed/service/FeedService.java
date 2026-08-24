package com.pamir.ppfarmsbackend.feed.service;

import com.pamir.ppfarmsbackend.feed.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedService {
    FeedInventoryResponse addOrUpdateFeedStock(FeedStockRequest request, UUID tenantId);
    List<FeedInventoryResponse> getFeedInventory(UUID tenantId);
    List<FeedInventoryResponse> getLowStockAlerts(UUID tenantId);
    FeedLogResponse logConsumption(FeedConsumptionRequest request, UUID tenantId);
    List<FeedLogResponse> getConsumptionLogs(UUID tenantId, LocalDate startDate, LocalDate endDate);
}
