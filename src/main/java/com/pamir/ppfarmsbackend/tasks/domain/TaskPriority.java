package com.pamir.ppfarmsbackend.tasks.domain;

import lombok.Getter;

@Getter
public enum TaskPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }
}
