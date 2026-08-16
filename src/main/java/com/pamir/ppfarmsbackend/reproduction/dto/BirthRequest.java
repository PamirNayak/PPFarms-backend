package com.pamir.ppfarmsbackend.reproduction.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BirthRequest {

    @NotNull(message = "Pregnancy ID is required")
    private UUID pregnancyId;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    private String deliveryNotes;

    @NotEmpty(message = "At least one offspring record must be provided")
    private List<OffspringDto> offspring;

    public UUID getPregnancyId() { return pregnancyId; } public void setPregnancyId(UUID pregnancyId) { this.pregnancyId = pregnancyId; }
    public LocalDate getBirthDate() { return birthDate; } public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getDeliveryNotes() { return deliveryNotes; } public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }
    public List<OffspringDto> getOffspring() { return offspring; } public void setOffspring(List<OffspringDto> offspring) { this.offspring = offspring; }

    @Data
    public static class OffspringDto {
        @NotNull(message = "Breed ID is required")
        private UUID breedId;

        private UUID shedPenId;

        @NotNull(message = "Ear tag number is required")
        private String tagNumber;

        private String name;

        @NotNull(message = "Gender is required")
        private String gender;

        private BigDecimal birthWeight;

        @NotNull(message = "Birth status is required")
        private String birthStatus; // ALIVE, STILLBORN

        public UUID getBreedId() { return breedId; } public void setBreedId(UUID breedId) { this.breedId = breedId; }
        public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
        public String getTagNumber() { return tagNumber; } public void setTagNumber(String tagNumber) { this.tagNumber = tagNumber; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
        public BigDecimal getBirthWeight() { return birthWeight; } public void setBirthWeight(BigDecimal birthWeight) { this.birthWeight = birthWeight; }
        public String getBirthStatus() { return birthStatus; } public void setBirthStatus(String birthStatus) { this.birthStatus = birthStatus; }
    }
}
