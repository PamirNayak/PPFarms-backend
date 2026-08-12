package com.pamir.ppfarmsbackend.herd.service;

import com.pamir.ppfarmsbackend.herd.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AnimalService {
    AnimalResponse createAnimal(AnimalRequest request, UUID tenantId);
    AnimalResponse updateAnimal(UUID id, AnimalRequest request, UUID tenantId);
    AnimalResponse getAnimalById(UUID id, UUID tenantId);
    Page<AnimalResponse> filterAnimals(UUID tenantId, UUID speciesId, UUID breedId, UUID shedPenId, String gender, String status, String search, Pageable pageable);
    PedigreeTreeResponse getPedigreeTree(UUID id, UUID tenantId);
    void logWeight(WeightRequest request, UUID tenantId);
    List<AnimalTimelineEventDto> getAnimalTimeline(UUID id, UUID tenantId);
}
