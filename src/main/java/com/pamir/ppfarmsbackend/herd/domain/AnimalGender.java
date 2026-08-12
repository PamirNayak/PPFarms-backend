package com.pamir.ppfarmsbackend.herd.domain;

import lombok.Getter;

@Getter
public enum AnimalGender {
    FEMALE("Female"),
    MALE("Male"),
    CASTRATED("Castrated");

    private final String displayName;

    AnimalGender(String displayName) {
        this.displayName = displayName;
    }
}
