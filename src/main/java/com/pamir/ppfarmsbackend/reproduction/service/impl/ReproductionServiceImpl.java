package com.pamir.ppfarmsbackend.reproduction.service.impl;

import com.pamir.ppfarmsbackend.herd.dto.AnimalRequest;
import com.pamir.ppfarmsbackend.herd.dto.AnimalResponse;
import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.service.AnimalService;
import com.pamir.ppfarmsbackend.reproduction.dto.*;
import com.pamir.ppfarmsbackend.reproduction.entity.*;
import com.pamir.ppfarmsbackend.reproduction.repository.*;
import com.pamir.ppfarmsbackend.reproduction.service.ReproductionService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReproductionServiceImpl implements ReproductionService {

    private final BreedingRepository breedingRepository;
    private final PregnancyRepository pregnancyRepository;
    private final BirthRecordRepository birthRecordRepository;
    private final BirthOffspringRepository birthOffspringRepository;
    private final AnimalRepository animalRepository;
    private final AnimalService animalService;

    @Override
    @Transactional
    public BreedingRecordResponse logBreeding(BreedingRequest request, UUID tenantId) {
        Animal dam = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getDamId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dam animal not found on this farm"));
        Animal sire = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getSireId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sire animal not found on this farm"));

        BreedingRecord record = BreedingRecord.builder()
                .organizationId(tenantId)
                .damId(dam.getId())
                .sireId(sire.getId())
                .breedingType(request.getBreedingType().toUpperCase())
                .bredAt(request.getBredAt())
                .outcome("PENDING_CONFIRMATION")
                .build();

        return BreedingRecordResponse.fromEntity(breedingRepository.save(record));
    }

    @Override
    public List<BreedingRecordResponse> getPendingMatings(UUID tenantId) {
        return breedingRepository.findByOrganizationIdAndOutcome(tenantId, "PENDING_CONFIRMATION")
                .stream()
                .map(BreedingRecordResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public PregnancyResponse confirmPregnancy(PregnancyConfirmRequest request, UUID tenantId) {
        BreedingRecord breedingRecord = breedingRepository.findById(request.getBreedingRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Breeding record not found"));

        if (!breedingRecord.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        Animal dam = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(breedingRecord.getDamId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dam animal not found on this farm"));

        int gestationDays = dam.getSpecies() != null ? dam.getSpecies().getGestationDays() : 150;
        LocalDate expectedDueDate = breedingRecord.getBredAt().plusDays(gestationDays);

        breedingRecord.setOutcome("PREGNANT");
        breedingRepository.save(breedingRecord);

        Pregnancy pregnancy = Pregnancy.builder()
                .organizationId(tenantId)
                .breedingRecordId(breedingRecord.getId())
                .animalId(dam.getId())
                .status("CONFIRMED")
                .confirmationDate(request.getConfirmationDate())
                .expectedDueDate(expectedDueDate)
                .build();

        return PregnancyResponse.fromEntity(pregnancyRepository.save(pregnancy));
    }

    @Override
    public List<PregnancyResponse> getActivePregnancies(UUID tenantId) {
        return pregnancyRepository.findByOrganizationIdAndStatus(tenantId, "CONFIRMED")
                .stream()
                .map(PregnancyResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public BirthRecordResponse recordBirth(BirthRequest request, UUID tenantId) {
        Pregnancy pregnancy = pregnancyRepository.findById(request.getPregnancyId())
                .orElseThrow(() -> new ResourceNotFoundException("Pregnancy record not found"));

        if (!pregnancy.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        BreedingRecord breedingRecord = breedingRepository.findById(pregnancy.getBreedingRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Breeding record not found"));

        pregnancy.setStatus("DELIVERED");
        pregnancy.setActualDeliveryDate(request.getBirthDate());
        pregnancyRepository.save(pregnancy);

        int totalBorn = request.getOffspring() != null ? request.getOffspring().size() : 0;
        int aliveCount = 0;
        int stillbornCount = 0;

        if (request.getOffspring() != null) {
            for (BirthRequest.OffspringDto dto : request.getOffspring()) {
                if ("ALIVE".equalsIgnoreCase(dto.getBirthStatus())) {
                    aliveCount++;
                } else {
                    stillbornCount++;
                }
            }
        }

        BirthRecord birthRecord = BirthRecord.builder()
                .organizationId(tenantId)
                .pregnancyId(pregnancy.getId())
                .damId(breedingRecord.getDamId())
                .sireId(breedingRecord.getSireId())
                .totalBorn(totalBorn)
                .aliveCount(aliveCount)
                .stillbornCount(stillbornCount)
                .birthDate(request.getBirthDate())
                .deliveryNotes(request.getDeliveryNotes())
                .build();

        birthRecord = birthRecordRepository.save(birthRecord);

        Animal dam = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(breedingRecord.getDamId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dam not found"));

        if (request.getOffspring() != null) {
            for (BirthRequest.OffspringDto dto : request.getOffspring()) {
                if ("ALIVE".equalsIgnoreCase(dto.getBirthStatus())) {
                    AnimalRequest animalReq = new AnimalRequest();
                    animalReq.setSpeciesId(dam.getSpecies() != null ? dam.getSpecies().getId() : null);
                    animalReq.setBreedId(dto.getBreedId());
                    animalReq.setShedPenId(dto.getShedPenId());
                    animalReq.setTagNumber(dto.getTagNumber());
                    animalReq.setName(dto.getName());
                    animalReq.setGender(dto.getGender());
                    animalReq.setStatus("ACTIVE");
                    animalReq.setDateOfBirth(request.getBirthDate());
                    animalReq.setBirthWeight(dto.getBirthWeight());
                    animalReq.setSireId(breedingRecord.getSireId());
                    animalReq.setDamId(breedingRecord.getDamId());

                    AnimalResponse createdAnimal = animalService.createAnimal(animalReq, tenantId);

                    BirthOffspring offspring = BirthOffspring.builder()
                            .birthRecordId(birthRecord.getId())
                            .animalId(createdAnimal.getId())
                            .gender(dto.getGender())
                            .birthWeight(dto.getBirthWeight())
                            .birthStatus("ALIVE")
                            .build();
                    birthOffspringRepository.save(offspring);
                }
            }
        }

        return BirthRecordResponse.fromEntity(birthRecord);
    }
}