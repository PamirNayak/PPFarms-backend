package com.pamir.ppfarmsbackend.crops.service;

import com.pamir.ppfarmsbackend.crops.dto.CropPlotRequest;
import com.pamir.ppfarmsbackend.crops.dto.CropPlotResponse;
import com.pamir.ppfarmsbackend.crops.dto.HarvestRequest;
import com.pamir.ppfarmsbackend.crops.dto.HarvestResponse;

import java.util.List;
import java.util.UUID;

public interface CropService {
    CropPlotResponse createPlot(CropPlotRequest request, UUID tenantId);
    CropPlotResponse updatePlot(UUID plotId, CropPlotRequest request, UUID tenantId);
    CropPlotResponse getPlotById(UUID plotId, UUID tenantId);
    List<CropPlotResponse> getAllPlots(UUID tenantId);
    void deletePlot(UUID plotId, UUID tenantId);

    HarvestResponse logHarvest(HarvestRequest request, UUID tenantId);
    List<HarvestResponse> getHarvestLogs(UUID tenantId, UUID plotId);
}
