package com.pamir.ppfarmsbackend.reproduction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PregnancyStatus {
    CONFIRMED("Pregnancy Confirmed"),
    DELIVERED("Delivered Offspring"),
    ABORTED("Aborted / Terminated"),
    FAILED("Failed Pregnancy");

    private final String displayName;
}
