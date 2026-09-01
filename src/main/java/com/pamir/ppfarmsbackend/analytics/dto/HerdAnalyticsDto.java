package com.pamir.ppfarmsbackend.analytics.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerdAnalyticsDto {

    private long totalAnimals;
    private long activeAnimals;
    private long soldAnimals;
    private long deceasedAnimals;
    private double mortalityRatePercentage;
    private Map<String, Long> animalsBySpecies;
    private Map<String, Long> animalsByGender;

    public long getTotalAnimals() { return totalAnimals; } public void setTotalAnimals(long totalAnimals) { this.totalAnimals = totalAnimals; }
    public long getActiveAnimals() { return activeAnimals; } public void setActiveAnimals(long activeAnimals) { this.activeAnimals = activeAnimals; }
    public long getSoldAnimals() { return soldAnimals; } public void setSoldAnimals(long soldAnimals) { this.soldAnimals = soldAnimals; }
    public long getDeceasedAnimals() { return deceasedAnimals; } public void setDeceasedAnimals(long deceasedAnimals) { this.deceasedAnimals = deceasedAnimals; }
    public double getMortalityRatePercentage() { return mortalityRatePercentage; } public void setMortalityRatePercentage(double mortalityRatePercentage) { this.mortalityRatePercentage = mortalityRatePercentage; }
    public Map<String, Long> getAnimalsBySpecies() { return animalsBySpecies; } public void setAnimalsBySpecies(Map<String, Long> animalsBySpecies) { this.animalsBySpecies = animalsBySpecies; }
    public Map<String, Long> getAnimalsByGender() { return animalsByGender; } public void setAnimalsByGender(Map<String, Long> animalsByGender) { this.animalsByGender = animalsByGender; }

    public static HerdAnalyticsDtoBuilder builder() { return new HerdAnalyticsDtoBuilder(); }
    public static class HerdAnalyticsDtoBuilder {
        private final HerdAnalyticsDto dto = new HerdAnalyticsDto();
        public HerdAnalyticsDtoBuilder totalAnimals(long totalAnimals) { dto.setTotalAnimals(totalAnimals); return this; }
        public HerdAnalyticsDtoBuilder activeAnimals(long activeAnimals) { dto.setActiveAnimals(activeAnimals); return this; }
        public HerdAnalyticsDtoBuilder soldAnimals(long soldAnimals) { dto.setSoldAnimals(soldAnimals); return this; }
        public HerdAnalyticsDtoBuilder deceasedAnimals(long deceasedAnimals) { dto.setDeceasedAnimals(deceasedAnimals); return this; }
        public HerdAnalyticsDtoBuilder mortalityRatePercentage(double mortalityRatePercentage) { dto.setMortalityRatePercentage(mortalityRatePercentage); return this; }
        public HerdAnalyticsDtoBuilder animalsBySpecies(Map<String, Long> animalsBySpecies) { dto.setAnimalsBySpecies(animalsBySpecies); return this; }
        public HerdAnalyticsDtoBuilder animalsByGender(Map<String, Long> animalsByGender) { dto.setAnimalsByGender(animalsByGender); return this; }
        public HerdAnalyticsDto build() { return dto; }
    }
}
