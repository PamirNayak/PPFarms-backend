package com.pamir.ppfarmsbackend.health.service.impl;

import com.pamir.ppfarmsbackend.health.dto.*;
import com.pamir.ppfarmsbackend.health.entity.DewormingRecord;
import com.pamir.ppfarmsbackend.health.entity.HealthRecord;
import com.pamir.ppfarmsbackend.health.entity.MortalityRecord;
import com.pamir.ppfarmsbackend.health.entity.VaccinationRecord;
import com.pamir.ppfarmsbackend.health.repository.DewormingRepository;
import com.pamir.ppfarmsbackend.health.repository.HealthRepository;
import com.pamir.ppfarmsbackend.health.repository.MortalityRepository;
import com.pamir.ppfarmsbackend.health.repository.VaccinationRepository;
import com.pamir.ppfarmsbackend.health.service.HealthService;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HealthServiceImpl implements HealthService {

    private final HealthRepository healthRepository;
    private final VaccinationRepository vaccinationRepository;
    private final DewormingRepository dewormingRepository;
    private final MortalityRepository mortalityRepository;
    private final AnimalRepository animalRepository;

    @Override
    @Transactional
    public HealthRecordResponse logHealthTreatment(HealthRequest request, UUID tenantId) {
        Animal animal = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getAnimalId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found on this farm"));

        HealthRecord record = HealthRecord.builder()
                .organizationId(tenantId)
                .animalId(animal.getId())
                .symptoms(request.getSymptoms())
                .treatment(request.getTreatment())
                .healthStatus(request.getHealthStatus().toUpperCase())
                .vetName(request.getVetName())
                .treatmentDate(request.getTreatmentDate())
                .followUpDate(request.getFollowUpDate())
                .milkWithdrawalUntilDate(request.getMilkWithdrawalUntilDate())
                .slaughterWithdrawalUntilDate(request.getSlaughterWithdrawalUntilDate())
                .build();

        return HealthRecordResponse.fromEntity(healthRepository.save(record));
    }

    @Override
    public List<HealthRecordResponse> getAnimalHealthHistory(UUID animalId, UUID tenantId) {
        return healthRepository.findByOrganizationIdAndOptionalAnimalId(tenantId, animalId)
                .stream()
                .map(HealthRecordResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public List<VaccinationRecordResponse> administerBatchVaccine(BatchVaccinationRequest request, UUID tenantId) {
        List<UUID> targetAnimalIds = new ArrayList<>();

        if (request.getAnimalIds() != null && !request.getAnimalIds().isEmpty()) {
            targetAnimalIds.addAll(request.getAnimalIds());
        } else if (request.getShedPenId() != null) {
            List<Animal> penAnimals = animalRepository.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get("organizationId"), tenantId),
                            cb.equal(root.get("shedPen").get("id"), request.getShedPenId()),
                            cb.isNull(root.get("deletedAt"))
                    ));
            for (Animal a : penAnimals) {
                targetAnimalIds.add(a.getId());
            }
        }

        if (targetAnimalIds.isEmpty()) {
            throw new BadRequestException("No target animals specified or found in shed/pen for batch vaccination");
        }

        List<VaccinationRecord> records = new ArrayList<>();
        for (UUID animalId : targetAnimalIds) {
            VaccinationRecord record = VaccinationRecord.builder()
                    .organizationId(tenantId)
                    .animalId(animalId)
                    .vaccineName(request.getVaccineName())
                    .batchNumber(request.getBatchNumber())
                    .dosage(request.getDosage())
                    .status("COMPLETED")
                    .administeredAt(request.getAdministeredAt())
                    .nextDueDate(request.getNextDueDate())
                    .build();
            records.add(record);
        }

        return vaccinationRepository.saveAll(records)
                .stream()
                .map(VaccinationRecordResponse::fromEntity)
                .toList();
    }

    @Override
    public List<VaccinationRecordResponse> getVaccinationHistory(UUID tenantId) {
        return vaccinationRepository.findByOrganizationId(tenantId)
                .stream()
                .map(VaccinationRecordResponse::fromEntity)
                .toList();
    }

    @Override
    public List<VaccinationRecordResponse> getUpcomingVaccinations(UUID tenantId, int days) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusDays(days > 0 ? days : 30);
        return vaccinationRepository.findByOrganizationIdAndNextDueDateBetween(tenantId, now, end)
                .stream()
                .map(VaccinationRecordResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public List<DewormingRecordResponse> administerDeworming(DewormingRequest request, UUID tenantId) {
        List<UUID> targetAnimalIds = new ArrayList<>();

        if (request.getAnimalIds() != null && !request.getAnimalIds().isEmpty()) {
            targetAnimalIds.addAll(request.getAnimalIds());
        } else if (request.getShedPenId() != null) {
            List<Animal> penAnimals = animalRepository.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get("organizationId"), tenantId),
                            cb.equal(root.get("shedPen").get("id"), request.getShedPenId()),
                            cb.isNull(root.get("deletedAt"))
                    ));
            for (Animal a : penAnimals) {
                targetAnimalIds.add(a.getId());
            }
        }

        if (targetAnimalIds.isEmpty()) {
            throw new BadRequestException("No target animals specified or found in shed/pen for deworming");
        }

        List<DewormingRecord> records = new ArrayList<>();
        for (UUID animalId : targetAnimalIds) {
            DewormingRecord record = DewormingRecord.builder()
                    .organizationId(tenantId)
                    .animalId(animalId)
                    .drugName(request.getDrugName())
                    .drugType(request.getDrugType() != null ? request.getDrugType() : "BENZIMIDAZOLE")
                    .dosage(request.getDosage())
                    .administeredAt(request.getAdministeredAt())
                    .nextDueDate(request.getNextDueDate())
                    .build();
            records.add(record);
        }

        return dewormingRepository.saveAll(records)
                .stream()
                .map(DewormingRecordResponse::fromEntity)
                .toList();
    }

    @Override
    public List<DewormingRecordResponse> getDewormingHistory(UUID tenantId) {
        return dewormingRepository.findByOrganizationId(tenantId)
                .stream()
                .map(DewormingRecordResponse::fromEntity)
                .toList();
    }

    @Override
    public List<DewormingRecordResponse> getUpcomingDewormings(UUID tenantId, int days) {
        LocalDate now = LocalDate.now();
        LocalDate end = now.plusDays(days > 0 ? days : 30);
        return dewormingRepository.findByOrganizationIdAndNextDueDateBetween(tenantId, now, end)
                .stream()
                .map(DewormingRecordResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MortalityRecordResponse logMortality(MortalityRequest request, UUID tenantId) {
        Animal animal = animalRepository.findByIdAndOrganizationIdAndDeletedAtIsNull(request.getAnimalId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found on this farm"));

        // Update animal status to DECEASED
        animal.setStatus(AnimalStatus.DECEASED);
        animalRepository.save(animal);

        MortalityRecord record = MortalityRecord.builder()
                .organizationId(tenantId)
                .animalId(animal.getId())
                .deathDate(request.getDeathDate())
                .causeOfDeath(request.getCauseOfDeath())
                .necropsyNotes(request.getNecropsyNotes())
                .disposalMethod(request.getDisposalMethod())
                .build();

        return MortalityRecordResponse.fromEntity(mortalityRepository.save(record));
    }

    @Override
    public List<MortalityRecordResponse> getMortalityRecords(UUID tenantId) {
        return mortalityRepository.findByOrganizationId(tenantId)
                .stream()
                .map(MortalityRecordResponse::fromEntity)
                .toList();
    }
}