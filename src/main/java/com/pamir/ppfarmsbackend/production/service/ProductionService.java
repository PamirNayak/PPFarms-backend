package com.pamir.ppfarmsbackend.production.service;

import com.pamir.ppfarmsbackend.production.dto.ProductionRecordRequest;
import com.pamir.ppfarmsbackend.production.dto.ProductionRecordResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProductionService {
    ProductionRecordResponse logProduction(ProductionRecordRequest request, UUID tenantId);
    List<ProductionRecordResponse> logBulkProduction(List<ProductionRecordRequest> requests, UUID tenantId);
    List<ProductionRecordResponse> getProductionHistory(UUID tenantId, LocalDate startDate, LocalDate endDate);
    List<ProductionRecordResponse> getAnimalProduction(UUID animalId, UUID tenantId);
}

