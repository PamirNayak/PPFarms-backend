package com.pamir.ppfarmsbackend.flock.service;

import com.pamir.ppfarmsbackend.flock.dto.EggLogRequest;
import com.pamir.ppfarmsbackend.flock.dto.FlockBatchRequest;
import com.pamir.ppfarmsbackend.flock.dto.FlockBatchResponse;
import com.pamir.ppfarmsbackend.flock.dto.MortalityLogRequest;
import com.pamir.ppfarmsbackend.flock.entity.EggProductionLog;
import com.pamir.ppfarmsbackend.flock.entity.FlockMortalityLog;

import java.util.List;
import java.util.UUID;

public interface FlockService {
    FlockBatchResponse createBatch(FlockBatchRequest request, UUID tenantId);
    List<FlockBatchResponse> getBatches(UUID tenantId);
    FlockBatchResponse getBatchById(UUID id, UUID tenantId);
    FlockBatchResponse updateBatch(UUID id, FlockBatchRequest request, UUID tenantId);
    void deleteBatch(UUID id, UUID tenantId);

    FlockMortalityLog logMortality(MortalityLogRequest request, UUID tenantId);
    List<FlockMortalityLog> getMortalityLogs(UUID flockBatchId, UUID tenantId);

    EggProductionLog logEggProduction(EggLogRequest request, UUID tenantId);
    List<EggProductionLog> getEggLogs(UUID flockBatchId, UUID tenantId);
}
