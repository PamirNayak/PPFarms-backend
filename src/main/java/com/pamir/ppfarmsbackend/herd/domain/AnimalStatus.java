package com.pamir.ppfarmsbackend.herd.domain;

import lombok.Getter;

@Getter
public enum AnimalStatus {
    ACTIVE("Active"),
    PREGNANT("Pregnant"),
    SICK("Sick"),
    QUARANTINED("Quarantined"),
    SOLD("Sold"),
    DECEASED("Deceased"),
    CULLED("Culled"),
    TRANSFERRED("Transferred"),
    RETIRED("Retired"),
    MISSING("Missing");

    private final String displayName;

    AnimalStatus(String displayName) {
        this.displayName = displayName;
    }
}
