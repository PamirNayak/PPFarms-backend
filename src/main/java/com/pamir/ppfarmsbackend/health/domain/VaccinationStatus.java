package com.pamir.ppfarmsbackend.health.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VaccinationStatus {
    SCHEDULED("Scheduled"),
    COMPLETED("Completed"),
    MISSED("Missed"),
    CANCELLED("Cancelled");

    private final String displayName;
}
