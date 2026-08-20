package com.pamir.ppfarmsbackend.health.domain;

import lombok.Getter;

@Getter
public enum HealthStatus {
    UNDER_TREATMENT("Under Treatment"),
    RECOVERED("Recovered"),
    CRITICAL("Critical"),
    CHRONIC("Chronic");

    private final String displayName;

    HealthStatus(String displayName) {
        this.displayName = displayName;
    }
}
