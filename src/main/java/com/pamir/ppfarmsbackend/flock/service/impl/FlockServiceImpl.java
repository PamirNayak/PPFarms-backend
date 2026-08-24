package com.pamir.ppfarmsbackend.flock.service.impl;

import com.pamir.ppfarmsbackend.flock.dto.*;
import com.pamir.ppfarmsbackend.flock.entity.EggProductionLog;
import com.pamir.ppfarmsbackend.flock.entity.FlockBatch;
import com.pamir.ppfarmsbackend.flock.entity.FlockMortalityLog;
import com.pamir.ppfarmsbackend.flock.repository.EggProductionLogRepository;
import com.pamir.ppfarmsbackend.flock.repository.FlockBatchRepository;
import com.pamir.ppfarmsbackend.flock.repository.FlockMortalityLogRepository;
import com.pamir.ppfarmsbackend.flock.service.FlockService;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.herd.repository.ShedPenRepository;
import com.pamir.ppfarmsbackend.herd.repository.SpeciesRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlockServiceImpl implements FlockService {

    private final FlockBatchRepository flockBatchRepository;
    private final FlockMortalityLogRepository mortalityLogRepository;
    private final EggProductionLogRepository eggLogRepository;
    private final SpeciesRepository speciesRepository;
    private final ShedPenRepository shedPenRepository;

    @Override
    public FlockBatchResponse createBatch(FlockBatchRequest request, UUID tenantId) {
        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species not found with ID " + request.getSpeciesId()));

        ShedPen shedPen = null;
        if (request.getShedPenId() != null) {
            shedPen = shedPenRepository.findById(request.getShedPenId()).orElse(null);
        }

        FlockBatch batch = FlockBatch.builder()
                .organizationId(tenantId)
                .batchName(request.getBatchName())
                .species(species)
                .shedPen(shedPen)
                .initialQuantity(request.getInitialQuantity())
                .currentQuantity(request.getInitialQuantity())
                .arrivalDate(request.getArrivalDate())
                .initialAgeWeeks(request.getInitialAgeWeeks() != null ? request.getInitialAgeWeeks() : 1)
                .purpose(request.getPurpose() != null ? request.getPurpose() : "DUAL")
                .status("ACTIVE")
                .purchaseCost(request.getPurchaseCost())
                .notes(request.getNotes())
                .build();

        batch = flockBatchRepository.save(batch);
        return mapToResponse(batch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlockBatchResponse> getBatches(UUID tenantId) {
        return flockBatchRepository.findByOrganizationIdAndDeletedAtIsNullOrderByArrivalDateDesc(tenantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FlockBatchResponse getBatchById(UUID id, UUID tenantId) {
        FlockBatch batch = flockBatchRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Flock batch not found"));
        return mapToResponse(batch);
    }

    @Override
    public FlockBatchResponse updateBatch(UUID id, FlockBatchRequest request, UUID tenantId) {
        FlockBatch batch = flockBatchRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Flock batch not found"));

        if (request.getBatchName() != null) batch.setBatchName(request.getBatchName());
        if (request.getShedPenId() != null) {
            ShedPen shedPen = shedPenRepository.findById(request.getShedPenId()).orElse(null);
            batch.setShedPen(shedPen);
        }
        if (request.getPurpose() != null) batch.setPurpose(request.getPurpose());
        if (request.getStatus() != null) batch.setStatus(request.getStatus());
        if (request.getNotes() != null) batch.setNotes(request.getNotes());

        batch = flockBatchRepository.save(batch);
        return mapToResponse(batch);
    }

    @Override
    public void deleteBatch(UUID id, UUID tenantId) {
        FlockBatch batch = flockBatchRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Flock batch not found"));
        batch.setDeletedAt(OffsetDateTime.now());
        flockBatchRepository.save(batch);
    }

    @Override
    public FlockMortalityLog logMortality(MortalityLogRequest request, UUID tenantId) {
        FlockBatch batch = flockBatchRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getFlockBatchId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Flock batch not found"));

        if (batch.getCurrentQuantity() < request.getDeadCount()) {
            throw new BadRequestException("Dead count cannot exceed current active bird count (" + batch.getCurrentQuantity() + ")");
        }

        // Decrement active flock count
        batch.setCurrentQuantity(batch.getCurrentQuantity() - request.getDeadCount());
        if (batch.getCurrentQuantity() == 0) {
            batch.setStatus("COMPLETED");
        }
        flockBatchRepository.save(batch);

        FlockMortalityLog log = FlockMortalityLog.builder()
                .organizationId(tenantId)
                .flockBatch(batch)
                .deadCount(request.getDeadCount())
                .logDate(request.getLogDate())
                .causeOfDeath(request.getCauseOfDeath())
                .notes(request.getNotes())
                .build();

        return mortalityLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlockMortalityLog> getMortalityLogs(UUID flockBatchId, UUID tenantId) {
        if (flockBatchId != null) {
            return mortalityLogRepository.findByOrganizationIdAndFlockBatchIdOrderByLogDateDesc(tenantId, flockBatchId);
        }
        return mortalityLogRepository.findByOrganizationIdOrderByLogDateDesc(tenantId);
    }

    @Override
    public EggProductionLog logEggProduction(EggLogRequest request, UUID tenantId) {
        FlockBatch batch = flockBatchRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getFlockBatchId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Flock batch not found"));

        int broken = request.getBrokenEggs() != null ? request.getBrokenEggs() : 0;
        int trays = request.getTraysCount() != null ? request.getTraysCount() : (request.getTotalEggs() / 30);

        EggProductionLog log = EggProductionLog.builder()
                .organizationId(tenantId)
                .flockBatch(batch)
                .collectionDate(request.getCollectionDate())
                .totalEggs(request.getTotalEggs())
                .brokenEggs(broken)
                .traysCount(trays)
                .notes(request.getNotes())
                .build();

        return eggLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EggProductionLog> getEggLogs(UUID flockBatchId, UUID tenantId) {
        if (flockBatchId != null) {
            return eggLogRepository.findByOrganizationIdAndFlockBatchIdOrderByCollectionDateDesc(tenantId, flockBatchId);
        }
        return eggLogRepository.findByOrganizationIdOrderByCollectionDateDesc(tenantId);
    }

    private FlockBatchResponse mapToResponse(FlockBatch batch) {
        int initial = batch.getInitialQuantity() != null ? batch.getInitialQuantity() : 0;
        int current = batch.getCurrentQuantity() != null ? batch.getCurrentQuantity() : 0;
        int dead = Math.max(0, initial - current);
        double mortalityRate = initial > 0 ? ((double) dead / initial) * 100.0 : 0.0;
        mortalityRate = Math.round(mortalityRate * 10.0) / 10.0;

        return FlockBatchResponse.builder()
                .id(batch.getId())
                .organizationId(batch.getOrganizationId())
                .batchName(batch.getBatchName())
                .speciesId(batch.getSpecies() != null ? batch.getSpecies().getId() : null)
                .speciesName(batch.getSpecies() != null ? batch.getSpecies().getName() : null)
                .shedPenId(batch.getShedPen() != null ? batch.getShedPen().getId() : null)
                .shedPenName(batch.getShedPen() != null ? batch.getShedPen().getName() : null)
                .initialQuantity(initial)
                .currentQuantity(current)
                .totalMortality(dead)
                .mortalityRatePercentage(mortalityRate)
                .arrivalDate(batch.getArrivalDate())
                .initialAgeWeeks(batch.getInitialAgeWeeks())
                .purpose(batch.getPurpose())
                .status(batch.getStatus())
                .purchaseCost(batch.getPurchaseCost())
                .notes(batch.getNotes())
                .createdAt(batch.getCreatedAt())
                .build();
    }
}
