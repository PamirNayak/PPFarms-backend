package com.pamir.ppfarmsbackend.reproduction.domain;

import lombok.Getter;

@Getter
public enum BreedingType {
    NATURAL("Natural Mating"),
    ARTIFICIAL_INSEMINATION("Artificial Insemination");

    private final String displayName;

    BreedingType(String displayName) {
        this.displayName = displayName;
    }
}
