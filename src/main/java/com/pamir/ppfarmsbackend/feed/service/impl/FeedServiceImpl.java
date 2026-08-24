package com.pamir.ppfarmsbackend.feed.service.impl;

import com.pamir.ppfarmsbackend.feed.dto.*;
import com.pamir.ppfarmsbackend.feed.entity.FeedConsumptionLog;
import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import com.pamir.ppfarmsbackend.feed.repository.FeedConsumptionLogRepository;
import com.pamir.ppfarmsbackend.feed.repository.FeedInventoryRepository;
import com.pamir.ppfarmsbackend.feed.service.FeedService;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.repository.ShedPenRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedInventoryRepository feedInventoryRepository;
    private final FeedConsumptionLogRepository feedConsumptionLogRepository;
    private final ShedPenRepository shedPenRepository;

    @Override
    @Transactional
    public FeedInventoryResponse addOrUpdateFeedStock(FeedStockRequest request, UUID tenantId) {
        FeedInventory item = FeedInventory.builder()
                .organizationId(tenantId)
                .feedName(request.getFeedName())
                .feedCategory(request.getFeedCategory().toUpperCase())
                .quantityKg(request.getQuantityKg())
                .costPerKg(request.getCostPerKg())
                .minThresholdKg(request.getMinThresholdKg())
                .build();

        item = feedInventoryRepository.save(item);
        return mapToInventoryResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedInventoryResponse> getFeedInventory(UUID tenantId) {
        return feedInventoryRepository.findByOrganizationIdAndDeletedAtIsNull(tenantId)
                .stream()
                .map(this::mapToInventoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedInventoryResponse> getLowStockAlerts(UUID tenantId) {
        return feedInventoryRepository.findLowStockItems(tenantId)
                .stream()
                .map(this::mapToInventoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public FeedLogResponse logConsumption(FeedConsumptionRequest request, UUID tenantId) {
        FeedInventory feed = feedInventoryRepository.findByIdAndOrganizationIdForUpdate(request.getFeedInventoryId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed inventory item not found"));

        if (feed.getQuantityKg().compareTo(request.getQuantityConsumedKg()) < 0) {
            throw new BadRequestException("Insufficient feed stock available. Current stock: " + feed.getQuantityKg() + " Kg");
        }

        // Deduct stock
        feed.setQuantityKg(feed.getQuantityKg().subtract(request.getQuantityConsumedKg()));
        feedInventoryRepository.save(feed);

        BigDecimal totalCost = request.getQuantityConsumedKg().multiply(feed.getCostPerKg());

        ShedPen shedPen = null;
        if (request.getShedPenId() != null) {
            shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen not found"));
        }

        FeedConsumptionLog log = FeedConsumptionLog.builder()
                .organizationId(tenantId)
                .feedInventoryId(feed.getId())
                .shedPenId(shedPen != null ? shedPen.getId() : null)
                .quantityConsumedKg(request.getQuantityConsumedKg())
                .totalCost(totalCost)
                .consumedDate(request.getConsumedDate())
                .build();

        log = feedConsumptionLogRepository.save(log);
        return mapToLogResponse(log, feed.getFeedName(), shedPen != null ? shedPen.getName() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedLogResponse> getConsumptionLogs(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        return feedConsumptionLogRepository.findByOrganizationIdAndConsumedDateBetween(tenantId, startDate, endDate)
                .stream()
                .map(log -> {
                    String feedName = feedInventoryRepository.findById(log.getFeedInventoryId()).map(FeedInventory::getFeedName).orElse(null);
                    String penName = log.getShedPenId() != null ? shedPenRepository.findById(log.getShedPenId()).map(ShedPen::getName).orElse(null) : null;
                    return mapToLogResponse(log, feedName, penName);
                })
                .toList();
    }

    private FeedInventoryResponse mapToInventoryResponse(FeedInventory feed) {
        boolean isLow = feed.getQuantityKg().compareTo(feed.getMinThresholdKg()) <= 0;
        return FeedInventoryResponse.builder()
                .id(feed.getId())
                .organizationId(feed.getOrganizationId())
                .feedName(feed.getFeedName())
                .feedCategory(feed.getFeedCategory())
                .quantityKg(feed.getQuantityKg())
                .costPerKg(feed.getCostPerKg())
                .minThresholdKg(feed.getMinThresholdKg())
                .isLowStock(isLow)
                .createdAt(feed.getCreatedAt())
                .build();
    }

    private FeedLogResponse mapToLogResponse(FeedConsumptionLog log, String feedName, String shedPenName) {
        return FeedLogResponse.builder()
                .id(log.getId())
                .organizationId(log.getOrganizationId())
                .feedInventoryId(log.getFeedInventoryId())
                .feedName(feedName)
                .shedPenId(log.getShedPenId())
                .shedPenName(shedPenName)
                .quantityConsumedKg(log.getQuantityConsumedKg())
                .totalCost(log.getTotalCost())
                .consumedDate(log.getConsumedDate())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
