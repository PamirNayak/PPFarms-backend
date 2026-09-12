package com.pamir.ppfarmsbackend.flock.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlockStatus {
    ACTIVE("Active Batch"),
    SOLD("Sold"),
    CULLED("Culled"),
    COMPLETED("Completed");

    private final String displayName;
}
