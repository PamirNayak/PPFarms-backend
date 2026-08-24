package com.pamir.ppfarmsbackend.crops.service.impl;

import com.pamir.ppfarmsbackend.crops.dto.CropPlotRequest;
import com.pamir.ppfarmsbackend.crops.dto.CropPlotResponse;
import com.pamir.ppfarmsbackend.crops.dto.HarvestRequest;
import com.pamir.ppfarmsbackend.crops.dto.HarvestResponse;
import com.pamir.ppfarmsbackend.crops.entity.CropHarvestLog;
import com.pamir.ppfarmsbackend.crops.entity.CropPlot;
import com.pamir.ppfarmsbackend.crops.repository.CropHarvestLogRepository;
import com.pamir.ppfarmsbackend.crops.repository.CropPlotRepository;
import com.pamir.ppfarmsbackend.crops.service.CropService;
import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import com.pamir.ppfarmsbackend.feed.repository.FeedInventoryRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropPlotRepository cropPlotRepository;
    private final CropHarvestLogRepository cropHarvestLogRepository;
    private final FeedInventoryRepository feedInventoryRepository;

    @Override
    @Transactional
    public CropPlotResponse createPlot(CropPlotRequest request, UUID tenantId) {
        CropPlot plot = CropPlot.builder()
                .organizationId(tenantId)
                .plotName(request.getPlotName())
                .areaAcres(request.getAreaAcres())
                .cropName(request.getCropName())
                .variety(request.getVariety())
                .sowingDate(request.getSowingDate())
                .expectedHarvestDate(request.getExpectedHarvestDate())
                .status(request.getStatus() != null ? request.getStatus() : "GROWING")
                .irrigationType(request.getIrrigationType() != null ? request.getIrrigationType() : "DRIP")
                .productionCost(request.getProductionCost() != null ? request.getProductionCost() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();

        plot = cropPlotRepository.save(plot);
        return mapToPlotResponse(plot);
    }

    @Override
    @Transactional
    public CropPlotResponse updatePlot(UUID plotId, CropPlotRequest request, UUID tenantId) {
        CropPlot plot = cropPlotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop plot not found"));

        if (!plot.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        plot.setPlotName(request.getPlotName());
        plot.setAreaAcres(request.getAreaAcres());
        plot.setCropName(request.getCropName());
        plot.setVariety(request.getVariety());
        plot.setSowingDate(request.getSowingDate());
        plot.setExpectedHarvestDate(request.getExpectedHarvestDate());
        if (request.getStatus() != null) plot.setStatus(request.getStatus());
        if (request.getIrrigationType() != null) plot.setIrrigationType(request.getIrrigationType());
        if (request.getProductionCost() != null) plot.setProductionCost(request.getProductionCost());
        plot.setNotes(request.getNotes());

        plot = cropPlotRepository.save(plot);
        return mapToPlotResponse(plot);
    }

    @Override
    @Transactional(readOnly = true)
    public CropPlotResponse getPlotById(UUID plotId, UUID tenantId) {
        CropPlot plot = cropPlotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop plot not found"));

        if (!plot.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        return mapToPlotResponse(plot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropPlotResponse> getAllPlots(UUID tenantId) {
        return cropPlotRepository.findByOrganizationIdAndDeletedAtIsNullOrderByCreatedAtDesc(tenantId)
                .stream().map(this::mapToPlotResponse).toList();
    }

    @Override
    @Transactional
    public void deletePlot(UUID plotId, UUID tenantId) {
        CropPlot plot = cropPlotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop plot not found"));

        if (!plot.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        cropPlotRepository.delete(plot);
    }

    @Override
    @Transactional
    public HarvestResponse logHarvest(HarvestRequest request, UUID tenantId) {
        CropPlot plot = cropPlotRepository.findById(request.getCropPlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Crop plot not found"));

        if (!plot.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        FeedInventory feed = null;
        if (request.getDestinationFeedId() != null) {
            feed = feedInventoryRepository.findById(request.getDestinationFeedId())
                    .orElseThrow(() -> new ResourceNotFoundException("Feed inventory destination not found"));

            // Automatically add harvested yield to feed stock
            feed.setQuantityKg(feed.getQuantityKg().add(request.getYieldKg()));
            feedInventoryRepository.save(feed);
        }

        CropHarvestLog harvest = CropHarvestLog.builder()
                .organizationId(tenantId)
                .cropPlot(plot)
                .harvestDate(request.getHarvestDate())
                .yieldKg(request.getYieldKg())
                .destinationFeed(feed)
                .storageLocation(request.getStorageLocation())
                .notes(request.getNotes())
                .build();

        harvest = cropHarvestLogRepository.save(harvest);

        // Update plot status
        plot.setStatus("HARVESTED");
        cropPlotRepository.save(plot);

        return mapToHarvestResponse(harvest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HarvestResponse> getHarvestLogs(UUID tenantId, UUID plotId) {
        List<CropHarvestLog> logs;
        if (plotId != null) {
            logs = cropHarvestLogRepository.findByOrganizationIdAndCropPlotIdOrderByHarvestDateDesc(tenantId, plotId);
        } else {
            logs = cropHarvestLogRepository.findByOrganizationIdOrderByHarvestDateDesc(tenantId);
        }
        return logs.stream().map(this::mapToHarvestResponse).toList();
    }

    private CropPlotResponse mapToPlotResponse(CropPlot plot) {
        List<CropHarvestLog> harvests = cropHarvestLogRepository.findByOrganizationIdAndCropPlotIdOrderByHarvestDateDesc(plot.getOrganizationId(), plot.getId());
        BigDecimal totalYield = harvests.stream()
                .map(CropHarvestLog::getYieldKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CropPlotResponse.builder()
                .id(plot.getId())
                .organizationId(plot.getOrganizationId())
                .plotName(plot.getPlotName())
                .areaAcres(plot.getAreaAcres())
                .cropName(plot.getCropName())
                .variety(plot.getVariety())
                .sowingDate(plot.getSowingDate())
                .expectedHarvestDate(plot.getExpectedHarvestDate())
                .status(plot.getStatus())
                .irrigationType(plot.getIrrigationType())
                .productionCost(plot.getProductionCost())
                .totalHarvestedYieldKg(totalYield)
                .notes(plot.getNotes())
                .createdAt(plot.getCreatedAt())
                .build();
    }

    private HarvestResponse mapToHarvestResponse(CropHarvestLog harvest) {
        return HarvestResponse.builder()
                .id(harvest.getId())
                .organizationId(harvest.getOrganizationId())
                .cropPlotId(harvest.getCropPlot().getId())
                .cropPlotName(harvest.getCropPlot().getPlotName())
                .cropName(harvest.getCropPlot().getCropName())
                .harvestDate(harvest.getHarvestDate())
                .yieldKg(harvest.getYieldKg())
                .destinationFeedId(harvest.getDestinationFeed() != null ? harvest.getDestinationFeed().getId() : null)
                .destinationFeedName(harvest.getDestinationFeed() != null ? harvest.getDestinationFeed().getFeedName() : null)
                .storageLocation(harvest.getStorageLocation())
                .notes(harvest.getNotes())
                .createdAt(harvest.getCreatedAt())
                .build();
    }
}
