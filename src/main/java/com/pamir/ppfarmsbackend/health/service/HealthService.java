package com.pamir.ppfarmsbackend.health.service;

import com.pamir.ppfarmsbackend.health.dto.*;

import java.util.List;
import java.util.UUID;

public interface HealthService {
    HealthRecordResponse logHealthTreatment(HealthRequest request, UUID tenantId);
    List<HealthRecordResponse> getAnimalHealthHistory(UUID animalId, UUID tenantId);
    List<VaccinationRecordResponse> administerBatchVaccine(BatchVaccinationRequest request, UUID tenantId);
    List<VaccinationRecordResponse> getVaccinationHistory(UUID tenantId);
    List<VaccinationRecordResponse> getUpcomingVaccinations(UUID tenantId, int days);

    List<DewormingRecordResponse> administerDeworming(DewormingRequest request, UUID tenantId);
    List<DewormingRecordResponse> getDewormingHistory(UUID tenantId);
    List<DewormingRecordResponse> getUpcomingDewormings(UUID tenantId, int days);

    MortalityRecordResponse logMortality(MortalityRequest request, UUID tenantId);
    List<MortalityRecordResponse> getMortalityRecords(UUID tenantId);
}