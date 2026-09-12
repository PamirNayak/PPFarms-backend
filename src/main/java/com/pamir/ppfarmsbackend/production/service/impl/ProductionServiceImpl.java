package com.pamir.ppfarmsbackend.production.service.impl;

import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.entity.ShedPen;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.repository.ShedPenRepository;
import com.pamir.ppfarmsbackend.production.dto.ProductionRecordRequest;
import com.pamir.ppfarmsbackend.production.dto.ProductionRecordResponse;
import com.pamir.ppfarmsbackend.production.entity.ProductionRecord;
import com.pamir.ppfarmsbackend.production.repository.ProductionRecordRepository;
import com.pamir.ppfarmsbackend.production.service.ProductionService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {

    private final ProductionRecordRepository productionRecordRepository;
    private final AnimalRepository animalRepository;
    private final ShedPenRepository shedPenRepository;

    @Override
    @Transactional
    public ProductionRecordResponse logProduction(ProductionRecordRequest request, UUID tenantId) {
        if (request.getQuantity() == null || request.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Production quantity must be greater than zero.");
        }

        Animal animal = null;
        if (request.getAnimalId() != null) {
            animal = animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
            if (!animal.getOrganizationId().equals(tenantId)) {
                throw new BadRequestException("Unauthorized access to animal");
            }
            if ("MILK".equalsIgnoreCase(request.getProductionType()) && animal.getGender() != com.pamir.ppfarmsbackend.herd.domain.AnimalGender.FEMALE) {
                throw new BadRequestException("Milk production can only be recorded for female animals (Current animal gender: " + animal.getGender() + ")");
            }
        }

        ShedPen shedPen = null;
        if (request.getShedPenId() != null) {
            shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen location not found"));
            if (!tenantId.equals(shedPen.getOrganizationId())) {
                throw new BadRequestException("Selected Shed/Pen does not belong to your farm.");
            }
        }

        ProductionRecord record = ProductionRecord.builder()
                .organizationId(tenantId)
                .animalId(animal != null ? animal.getId() : null)
                .shedPenId(shedPen != null ? shedPen.getId() : null)
                .productionType(request.getProductionType().toUpperCase())
                .quantity(request.getQuantity())
                .unit(request.getUnit() != null ? request.getUnit().toUpperCase() : "LITERS")
                .fatPercentage(request.getFatPercentage())
                .snfPercentage(request.getSnfPercentage())
                .recordedDate(request.getRecordedDate())
                .notes(request.getNotes())
                .build();

        record = productionRecordRepository.save(record);
        return mapToResponse(record);
    }

    @Override
    @Transactional
    public List<ProductionRecordResponse> logBulkProduction(List<ProductionRecordRequest> requests, UUID tenantId) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(req -> logProduction(req, tenantId))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProductionRecordResponse> getProductionHistory(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        return productionRecordRepository.findByOrganizationIdAndRecordedDateBetween(tenantId, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductionRecordResponse> getAnimalProduction(UUID animalId, UUID tenantId) {
        return productionRecordRepository.findByOrganizationIdAndAnimalId(tenantId, animalId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProductionRecordResponse mapToResponse(ProductionRecord record) {
        String animalTag = null;
        UUID speciesId = null;
        String speciesName = null;

        if (record.getAnimalId() != null) {
            Animal animal = animalRepository.findById(record.getAnimalId()).orElse(null);
            if (animal != null) {
                animalTag = animal.getTagNumber();
                if (animal.getSpecies() != null) {
                    speciesId = animal.getSpecies().getId();
                    speciesName = animal.getSpecies().getName();
                }
            }
        }

        String penName = null;
        if (record.getShedPenId() != null) {
            penName = shedPenRepository.findById(record.getShedPenId()).map(ShedPen::getName).orElse(null);
        }

        return ProductionRecordResponse.builder()
                .id(record.getId())
                .organizationId(record.getOrganizationId())
                .animalId(record.getAnimalId())
                .animalTagNumber(animalTag)
                .speciesId(speciesId)
                .speciesName(speciesName)
                .shedPenId(record.getShedPenId())
                .shedPenName(penName)
                .productionType(record.getProductionType())
                .quantity(record.getQuantity())
                .unit(record.getUnit())
                .fatPercentage(record.getFatPercentage())
                .snfPercentage(record.getSnfPercentage())
                .recordedDate(record.getRecordedDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
