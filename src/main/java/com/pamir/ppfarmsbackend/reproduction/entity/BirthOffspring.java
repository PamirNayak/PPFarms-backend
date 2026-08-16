package com.pamir.ppfarmsbackend.reproduction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "birth_offspring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BirthOffspring {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "birth_record_id", nullable = false)
    private UUID birthRecordId;

    @Column(name = "animal_id", nullable = false)
    private UUID animalId;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(name = "birth_weight", precision = 7, scale = 2)
    private BigDecimal birthWeight;

    @Column(name = "birth_status", nullable = false, length = 30)
    private String birthStatus; // ALIVE, STILLBORN

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getBirthRecordId() { return birthRecordId; } public void setBirthRecordId(UUID birthRecordId) { this.birthRecordId = birthRecordId; }
    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
    public BigDecimal getBirthWeight() { return birthWeight; } public void setBirthWeight(BigDecimal birthWeight) { this.birthWeight = birthWeight; }
    public String getBirthStatus() { return birthStatus; } public void setBirthStatus(String birthStatus) { this.birthStatus = birthStatus; }

    public static BirthOffspringBuilder builder() { return new BirthOffspringBuilder(); }
    public static class BirthOffspringBuilder {
        private final BirthOffspring bo = new BirthOffspring();
        public BirthOffspringBuilder id(UUID id) { bo.setId(id); return this; }
        public BirthOffspringBuilder birthRecordId(UUID birthRecordId) { bo.setBirthRecordId(birthRecordId); return this; }
        public BirthOffspringBuilder animalId(UUID animalId) { bo.setAnimalId(animalId); return this; }
        public BirthOffspringBuilder gender(String gender) { bo.setGender(gender); return this; }
        public BirthOffspringBuilder birthWeight(BigDecimal birthWeight) { bo.setBirthWeight(birthWeight); return this; }
        public BirthOffspringBuilder birthStatus(String birthStatus) { bo.setBirthStatus(birthStatus); return this; }
        public BirthOffspring build() { return bo; }
    }
}
