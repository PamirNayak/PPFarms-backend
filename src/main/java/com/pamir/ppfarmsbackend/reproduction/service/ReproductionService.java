package com.pamir.ppfarmsbackend.reproduction.service;

import com.pamir.ppfarmsbackend.reproduction.dto.*;

import java.util.List;
import java.util.UUID;

public interface ReproductionService {
    BreedingRecordResponse logBreeding(BreedingRequest request, UUID tenantId);
    List<BreedingRecordResponse> getPendingMatings(UUID tenantId);
    PregnancyResponse confirmPregnancy(PregnancyConfirmRequest request, UUID tenantId);
    List<PregnancyResponse> getActivePregnancies(UUID tenantId);
    BirthRecordResponse recordBirth(BirthRequest request, UUID tenantId);
}