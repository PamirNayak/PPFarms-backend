package com.pamir.ppfarmsbackend.herd.service.impl;

import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.health.entity.*;
import com.pamir.ppfarmsbackend.health.repository.*;
import com.pamir.ppfarmsbackend.herd.domain.AnimalGender;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.herd.dto.*;
import com.pamir.ppfarmsbackend.herd.entity.*;
import com.pamir.ppfarmsbackend.herd.repository.*;
import com.pamir.ppfarmsbackend.herd.service.AnimalService;
import com.pamir.ppfarmsbackend.production.entity.ProductionRecord;
import com.pamir.ppfarmsbackend.production.repository.ProductionRecordRepository;
import com.pamir.ppfarmsbackend.reproduction.entity.*;
import com.pamir.ppfarmsbackend.reproduction.repository.*;
import com.pamir.ppfarmsbackend.sales.entity.SaleItem;
import com.pamir.ppfarmsbackend.sales.repository.SaleItemRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.PlanQuotaExceededException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final ShedPenRepository shedPenRepository;
    private final WeightRepository weightRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final VaccinationRepository vaccinationRepository;
    private final HealthRepository healthRepository;
    private final DewormingRepository dewormingRepository;
    private final MortalityRepository mortalityRepository;
    private final BreedingRepository breedingRepository;
    private final PregnancyRepository pregnancyRepository;
    private final BirthRecordRepository birthRecordRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final SaleItemRepository saleItemRepository;

    @Override
    @Transactional
    public AnimalResponse createAnimal(AnimalRequest request, UUID tenantId) {
        Subscription subscription = subscriptionRepository.findByOrganizationId(tenantId)
                .orElseThrow(() -> new BadRequestException("No active subscription found for farm. Please subscribe to a plan."));

        if ("EXPIRED".equalsIgnoreCase(subscription.getStatus()) || (subscription.getEndDate() != null && subscription.getEndDate().isBefore(java.time.LocalDate.now()))) {
            throw new BadRequestException("Your subscription has expired. Account is in Read-Only mode. Please renew your subscription to perform write operations.");
        }

        long currentAnimalCount = animalRepository.countByOrganizationIdAndDeletedAtIsNull(tenantId);
        if (currentAnimalCount >= subscription.getPlan().getMaxAnimals()) {
            throw new PlanQuotaExceededException("Animal count limit of " + subscription.getPlan().getMaxAnimals() + " reached for your " + subscription.getPlan().getName() + " plan. Please upgrade your plan.");
        }

        if (animalRepository.existsByOrganizationIdAndTagNumberAndDeletedAtIsNull(tenantId, request.getTagNumber())) {
            throw new BadRequestException("Ear Tag number '" + request.getTagNumber() + "' is already registered in your farm");
        }

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species not found"));

        // Validate species entitlement against active subscription plan features
        if (subscription.getPlan() != null && subscription.getPlan().getFeatures() != null) {
            String features = subscription.getPlan().getFeatures().toUpperCase();
            String speciesUpper = species.getName().toUpperCase();
            if (!features.contains("\"ALLOWEDSPECIES\":[\"ALL\"]") && !features.contains("\"" + speciesUpper + "\"")) {
                throw new BadRequestException("Your active subscription tier (" + subscription.getPlan().getName() + ") does not support " + species.getName() + " management. Please upgrade your subscription plan.");
            }
        }

        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found"));

        ShedPen shedPen = null;
        if (request.getShedPenId() != null) {
            shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen location not found"));
            if (!tenantId.equals(shedPen.getOrganizationId())) {
                throw new BadRequestException("Selected Shed/Pen does not belong to your farm.");
            }
        }

        Animal sire = null;
        if (request.getSireId() != null) {
            sire = animalRepository.findById(request.getSireId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sire (father) animal not found"));
            if (!tenantId.equals(sire.getOrganizationId())) {
                throw new BadRequestException("Selected Sire does not belong to your farm.");
            }
        }

        Animal dam = null;
        if (request.getDamId() != null) {
            dam = animalRepository.findById(request.getDamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dam (mother) animal not found"));
            if (!tenantId.equals(dam.getOrganizationId())) {
                throw new BadRequestException("Selected Dam does not belong to your farm.");
            }
        }

        AnimalGender gender = parseGender(request.getGender());
        AnimalStatus status = request.getStatus() != null ? parseStatus(request.getStatus()) : AnimalStatus.ACTIVE;

        Animal animal = Animal.builder()
                .organizationId(tenantId)
                .species(species)
                .breed(breed)
                .shedPen(shedPen)
                .tagNumber(request.getTagNumber())
                .name(request.getName())
                .gender(gender)
                .status(status)
                .dateOfBirth(request.getDateOfBirth())
                .birthWeight(request.getBirthWeight())
                .sire(sire)
                .dam(dam)
                .color(request.getColor())
                .height(request.getHeight())
                .purchasePrice(request.getPurchasePrice())
                .purchaseDate(request.getPurchaseDate())
                .source(request.getSource())
                .photoUrl(request.getPhotoUrl())
                .build();

        animal = animalRepository.save(animal);

        if (request.getBirthWeight() != null && request.getBirthWeight().compareTo(BigDecimal.ZERO) > 0) {
            WeightRecord weightRecord = WeightRecord.builder()
                    .organizationId(tenantId)
                    .animalId(animal.getId())
                    .weightKg(request.getBirthWeight())
                    .measuredAt(request.getDateOfBirth() != null ? request.getDateOfBirth() : animal.getCreatedAt().toLocalDate())
                    .notes("Initial birth weight")
                    .build();
            weightRepository.save(weightRecord);
        }

        return mapToResponse(animal);
    }

    @Override
    @Transactional
    public AnimalResponse updateAnimal(UUID id, AnimalRequest request, UUID tenantId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access to animal profile");
        }

        if (request.getTagNumber() != null && !request.getTagNumber().equalsIgnoreCase(animal.getTagNumber())) {
            animalRepository.findByOrganizationIdAndTagNumberAndDeletedAtIsNull(tenantId, request.getTagNumber())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new BadRequestException("Ear Tag number '" + request.getTagNumber() + "' is already in use by another animal in your farm.");
                        }
                    });
        }

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new ResourceNotFoundException("Species not found"));
        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found"));

        if (request.getShedPenId() != null) {
            ShedPen shedPen = shedPenRepository.findById(request.getShedPenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shed/Pen location not found"));
            if (!tenantId.equals(shedPen.getOrganizationId())) {
                throw new BadRequestException("Selected Shed/Pen does not belong to your farm.");
            }
            animal.setShedPen(shedPen);
        } else {
            animal.setShedPen(null);
        }

        animal.setSpecies(species);
        animal.setBreed(breed);
        animal.setTagNumber(request.getTagNumber());
        animal.setName(request.getName());
        animal.setGender(parseGender(request.getGender()));
        animal.setStatus(parseStatus(request.getStatus()));
        animal.setDateOfBirth(request.getDateOfBirth());
        animal.setColor(request.getColor());
        animal.setHeight(request.getHeight());
        animal.setPurchasePrice(request.getPurchasePrice());
        animal.setPurchaseDate(request.getPurchaseDate());
        animal.setSource(request.getSource());
        animal.setPhotoUrl(request.getPhotoUrl());

        animal = animalRepository.save(animal);
        return mapToResponse(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalResponse getAnimalById(UUID id, UUID tenantId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access to animal profile");
        }

        return mapToResponse(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalResponse> filterAnimals(UUID tenantId, UUID speciesId, UUID breedId, UUID shedPenId, String gender, String status, String search, Pageable pageable) {
        Specification<Animal> spec = AnimalSpecification.filterAnimals(tenantId, speciesId, breedId, shedPenId, gender, status, search);
        return animalRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PedigreeTreeResponse getPedigreeTree(UUID id, UUID tenantId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        return PedigreeTreeResponse.builder()
                .id(animal.getId())
                .tagNumber(animal.getTagNumber())
                .name(animal.getName())
                .gender(animal.getGender() != null ? animal.getGender().name() : null)
                .breedName(animal.getBreed().getName())
                .speciesName(animal.getSpecies().getName())
                .sire(buildParentInfo(animal.getSire()))
                .dam(buildParentInfo(animal.getDam()))
                .build();
    }

    @Override
    @Transactional
    public void logWeight(WeightRequest request, UUID tenantId) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!animal.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        WeightRecord weightRecord = WeightRecord.builder()
                .organizationId(tenantId)
                .animalId(animal.getId())
                .weightKg(request.getWeightKg())
                .measuredAt(request.getMeasuredAt())
                .notes(request.getNotes())
                .build();

        weightRepository.save(weightRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalTimelineEventDto> getAnimalTimeline(UUID id, UUID tenantId) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id " + id));

        if (!animal.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        List<AnimalTimelineEventDto> timeline = new ArrayList<>();

        // 1. Birth Event
        if (animal.getDateOfBirth() != null) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("birthWeight", animal.getBirthWeight());
            meta.put("sire", animal.getSire() != null ? animal.getSire().getTagNumber() : "Unknown");
            meta.put("dam", animal.getDam() != null ? animal.getDam().getTagNumber() : "Unknown");

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("BIRTH")
                    .eventDate(animal.getDateOfBirth())
                    .title("Born & Registered")
                    .description("Born with tag " + animal.getTagNumber() + " (Breed: " + animal.getBreed().getName() + ")")
                    .category("REPRODUCTION")
                    .badgeColor("GREEN")
                    .metadata(meta)
                    .build());
        }

        // 2. Weight Measurements
        List<WeightRecord> weights = weightRepository.findByOrganizationIdAndAnimalIdOrderByMeasuredAtDesc(tenantId, id);
        for (WeightRecord w : weights) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("weightKg", w.getWeightKg());
            meta.put("notes", w.getNotes());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("WEIGHT_RECORD")
                    .eventDate(w.getMeasuredAt())
                    .title("Scale Weight Recorded: " + w.getWeightKg() + " kg")
                    .description(w.getNotes() != null ? w.getNotes() : "Routine scale weight measurement")
                    .category("GROWTH")
                    .badgeColor("BLUE")
                    .metadata(meta)
                    .build());
        }

        // 3. Vaccinations
        List<VaccinationRecord> vaccinations = vaccinationRepository.findByOrganizationIdAndAnimalId(tenantId, id);
        for (VaccinationRecord v : vaccinations) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("vaccineName", v.getVaccineName());
            meta.put("batchNumber", v.getBatchNumber());
            meta.put("nextDueDate", v.getNextDueDate());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("VACCINATION")
                    .eventDate(v.getAdministeredAt())
                    .title("Vaccination: " + v.getVaccineName())
                    .description("Administered dose: " + v.getDosage() + ". Next due: " + v.getNextDueDate())
                    .category("HEALTH")
                    .badgeColor("PURPLE")
                    .metadata(meta)
                    .build());
        }

        // 4. Dewormings
        List<DewormingRecord> dewormings = dewormingRepository.findByOrganizationIdAndAnimalId(tenantId, id);
        for (DewormingRecord d : dewormings) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("drugName", d.getDrugName());
            meta.put("dosage", d.getDosage());
            meta.put("nextDueDate", d.getNextDueDate());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("DEWORMING")
                    .eventDate(d.getAdministeredAt())
                    .title("Deworming: " + d.getDrugName())
                    .description("Dosage: " + d.getDosage() + ". Next due: " + d.getNextDueDate())
                    .category("HEALTH")
                    .badgeColor("PURPLE")
                    .metadata(meta)
                    .build());
        }

        // 5. Medical Treatments
        List<HealthRecord> healths = healthRepository.findByOrganizationIdAndOptionalAnimalId(tenantId, id);
        for (HealthRecord h : healths) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("symptoms", h.getSymptoms());
            meta.put("treatment", h.getTreatment());
            meta.put("vetName", h.getVetName());
            meta.put("healthStatus", h.getHealthStatus());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("TREATMENT")
                    .eventDate(h.getTreatmentDate())
                    .title("Veterinary Treatment (" + h.getHealthStatus() + ")")
                    .description(h.getSymptoms() + " -> " + h.getTreatment() + " (Vet: " + (h.getVetName() != null ? h.getVetName() : "In-house") + ")")
                    .category("HEALTH")
                    .badgeColor("RED")
                    .metadata(meta)
                    .build());
        }

        // 6. Breeding Matings (As Dam or Sire)
        List<BreedingRecord> matings = breedingRepository.findByOrganizationIdAndDamIdOrderByBredAtDesc(tenantId, id);
        for (BreedingRecord b : matings) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("breedingType", b.getBreedingType());
            meta.put("outcome", b.getOutcome());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("MATING")
                    .eventDate(b.getBredAt())
                    .title("Breeding Mating (" + b.getBreedingType() + ")")
                    .description("Mated. Outcome: " + b.getOutcome())
                    .category("REPRODUCTION")
                    .badgeColor("ORANGE")
                    .metadata(meta)
                    .build());
        }

        // 7. Pregnancies Confirmed
        List<Pregnancy> pregnancies = pregnancyRepository.findByOrganizationIdAndAnimalId(tenantId, id);
        for (Pregnancy p : pregnancies) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("expectedDueDate", p.getExpectedDueDate());
            meta.put("status", p.getStatus());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("PREGNANCY")
                    .eventDate(p.getConfirmationDate())
                    .title("Pregnancy Confirmed (" + p.getStatus() + ")")
                    .description("Expected Delivery Due Date: " + p.getExpectedDueDate())
                    .category("REPRODUCTION")
                    .badgeColor("GREEN")
                    .metadata(meta)
                    .build());
        }

        // 8. Kidding / Calving Deliveries
        List<BirthRecord> deliveries = birthRecordRepository.findByOrganizationIdAndDamId(tenantId, id);
        for (BirthRecord br : deliveries) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("totalBorn", br.getTotalBorn());
            meta.put("aliveCount", br.getAliveCount());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("KIDDING")
                    .eventDate(br.getBirthDate())
                    .title("Offspring Delivery: " + br.getAliveCount() + " Kids/Calves Born")
                    .description(br.getDeliveryNotes() != null ? br.getDeliveryNotes() : "Delivered " + br.getTotalBorn() + " total offspring")
                    .category("REPRODUCTION")
                    .badgeColor("GREEN")
                    .metadata(meta)
                    .build());
        }

        // 9. Mortality (if deceased)
        List<MortalityRecord> mort = mortalityRepository.findByOrganizationIdAndAnimalId(tenantId, id);
        if (!mort.isEmpty()) {
            MortalityRecord m = mort.get(0);
            Map<String, Object> meta = new HashMap<>();
            meta.put("causeOfDeath", m.getCauseOfDeath());
            meta.put("disposalMethod", m.getDisposalMethod());

            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("MORTALITY")
                    .eventDate(m.getDeathDate())
                    .title("Deceased: " + m.getCauseOfDeath())
                    .description("Disposal: " + m.getDisposalMethod() + ". Notes: " + m.getNecropsyNotes())
                    .category("HEALTH")
                    .badgeColor("RED")
                    .metadata(meta)
                    .build());
        }

        // 10. Sale (if sold)
        List<SaleItem> soldItems = saleItemRepository.findAll().stream()
                .filter(si -> id.equals(si.getAnimalId())).toList();
        for (SaleItem si : soldItems) {
            timeline.add(AnimalTimelineEventDto.builder()
                    .eventType("SALE")
                    .eventDate(si.getSale() != null ? si.getSale().getSaleDate() : LocalDate.now())
                    .title("Sold for " + (si.getTotalPrice() != null ? si.getTotalPrice().toString() : "N/A"))
                    .description("Sold to: " + (si.getSale() != null ? si.getSale().getBuyerName() : "Buyer"))
                    .category("COMMERCIAL")
                    .badgeColor("BLUE")
                    .metadata(new HashMap<>())
                    .build());
        }

        // Sort timeline chronologically descending (newest first)
        timeline.sort((a, b) -> b.getEventDate().compareTo(a.getEventDate()));

        return timeline;
    }

    private AnimalGender parseGender(String genderStr) {
        if (genderStr == null) return AnimalGender.FEMALE;
        try {
            return AnimalGender.valueOf(genderStr.trim().toUpperCase());
        } catch (Exception e) {
            return AnimalGender.FEMALE;
        }
    }

    private AnimalStatus parseStatus(String statusStr) {
        if (statusStr == null) return AnimalStatus.ACTIVE;
        try {
            return AnimalStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (Exception e) {
            return AnimalStatus.ACTIVE;
        }
    }

    private PedigreeTreeResponse.ParentInfo buildParentInfo(Animal parent) {
        if (parent == null) return null;
        return PedigreeTreeResponse.ParentInfo.builder()
                .id(parent.getId())
                .tagNumber(parent.getTagNumber())
                .name(parent.getName())
                .breedName(parent.getBreed() != null ? parent.getBreed().getName() : null)
                .sire(buildParentInfo(parent.getSire()))
                .dam(buildParentInfo(parent.getDam()))
                .build();
    }

    private AnimalResponse mapToResponse(Animal animal) {
        Optional<WeightRecord> latestWeight = weightRepository.findFirstByOrganizationIdAndAnimalIdOrderByMeasuredAtDesc(animal.getOrganizationId(), animal.getId());
        BigDecimal currentWeight = latestWeight.map(WeightRecord::getWeightKg).orElse(animal.getBirthWeight());

        Double adgGrams = null;
        String performance = "NORMAL";

        if (animal.getDateOfBirth() != null && currentWeight != null && animal.getBirthWeight() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(animal.getDateOfBirth(), LocalDate.now());
            if (days > 0 && currentWeight.compareTo(animal.getBirthWeight()) > 0) {
                BigDecimal gainKg = currentWeight.subtract(animal.getBirthWeight());
                double adg = (gainKg.doubleValue() / days) * 1000.0;
                adgGrams = Math.round(adg * 10.0) / 10.0;

                if (adgGrams >= 150.0) {
                    performance = "FAST_GROWTH";
                } else if (adgGrams < 60.0) {
                    performance = "STUNTED";
                } else {
                    performance = "NORMAL";
                }
            }
        }

        return AnimalResponse.builder()
                .id(animal.getId())
                .organizationId(animal.getOrganizationId())
                .speciesId(animal.getSpecies().getId())
                .speciesName(animal.getSpecies().getName())
                .breedId(animal.getBreed().getId())
                .breedName(animal.getBreed().getName())
                .shedPenId(animal.getShedPen() != null ? animal.getShedPen().getId() : null)
                .shedPenName(animal.getShedPen() != null ? animal.getShedPen().getName() : null)
                .tagNumber(animal.getTagNumber())
                .name(animal.getName())
                .gender(animal.getGender() != null ? animal.getGender().name() : null)
                .status(animal.getStatus() != null ? animal.getStatus().name() : null)
                .dateOfBirth(animal.getDateOfBirth())
                .birthWeight(animal.getBirthWeight())
                .currentWeight(currentWeight)
                .averageDailyGainGrams(adgGrams)
                .growthPerformance(performance)
                .color(animal.getColor())
                .height(animal.getHeight())
                .purchasePrice(animal.getPurchasePrice())
                .purchaseDate(animal.getPurchaseDate())
                .source(animal.getSource())
                .sireId(animal.getSire() != null ? animal.getSire().getId() : null)
                .sireTagNumber(animal.getSire() != null ? animal.getSire().getTagNumber() : null)
                .damId(animal.getDam() != null ? animal.getDam().getId() : null)
                .damTagNumber(animal.getDam() != null ? animal.getDam().getTagNumber() : null)
                .photoUrl(animal.getPhotoUrl())
                .createdAt(animal.getCreatedAt())
                .build();
    }
}
