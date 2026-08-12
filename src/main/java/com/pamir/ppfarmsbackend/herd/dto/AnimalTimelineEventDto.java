package com.pamir.ppfarmsbackend.herd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTimelineEventDto {

    private String eventType; // BIRTH, WEIGHT_RECORD, VACCINATION, DEWORMING, TREATMENT, HEAT_CYCLE, MATING, PREGNANCY, KIDDING, MILK_PRODUCTION, SALE, MORTALITY
    private LocalDate eventDate;
    private String title;
    private String description;
    private String category; // HEALTH, GROWTH, REPRODUCTION, PRODUCTION, COMMERCIAL
    private String badgeColor; // BLUE, GREEN, ORANGE, RED, PURPLE
    private Map<String, Object> metadata;
}
