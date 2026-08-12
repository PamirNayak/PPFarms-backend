package com.pamir.ppfarmsbackend.herd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedigreeTreeResponse {

    private UUID id;
    private String tagNumber;
    private String name;
    private String gender;
    private String breedName;
    private String speciesName;
    private ParentInfo sire;
    private ParentInfo dam;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getTagNumber() { return tagNumber; } public void setTagNumber(String tagNumber) { this.tagNumber = tagNumber; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getGender() { return gender; } public void setGender(String gender) { this.gender = gender; }
    public String getBreedName() { return breedName; } public void setBreedName(String breedName) { this.breedName = breedName; }
    public String getSpeciesName() { return speciesName; } public void setSpeciesName(String speciesName) { this.speciesName = speciesName; }
    public ParentInfo getSire() { return sire; } public void setSire(ParentInfo sire) { this.sire = sire; }
    public ParentInfo getDam() { return dam; } public void setDam(ParentInfo dam) { this.dam = dam; }

    public static PedigreeTreeResponseBuilder builder() { return new PedigreeTreeResponseBuilder(); }
    public static class PedigreeTreeResponseBuilder {
        private final PedigreeTreeResponse res = new PedigreeTreeResponse();
        public PedigreeTreeResponseBuilder id(UUID id) { res.setId(id); return this; }
        public PedigreeTreeResponseBuilder tagNumber(String tagNumber) { res.setTagNumber(tagNumber); return this; }
        public PedigreeTreeResponseBuilder name(String name) { res.setName(name); return this; }
        public PedigreeTreeResponseBuilder gender(String gender) { res.setGender(gender); return this; }
        public PedigreeTreeResponseBuilder breedName(String breedName) { res.setBreedName(breedName); return this; }
        public PedigreeTreeResponseBuilder speciesName(String speciesName) { res.setSpeciesName(speciesName); return this; }
        public PedigreeTreeResponseBuilder sire(ParentInfo sire) { res.setSire(sire); return this; }
        public PedigreeTreeResponseBuilder dam(ParentInfo dam) { res.setDam(dam); return this; }
        public PedigreeTreeResponse build() { return res; }
    }

    public static class ParentInfo {
        private UUID id;
        private String tagNumber;
        private String name;
        private String breedName;
        private ParentInfo sire;
        private ParentInfo dam;

        public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
        public String getTagNumber() { return tagNumber; } public void setTagNumber(String tagNumber) { this.tagNumber = tagNumber; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getBreedName() { return breedName; } public void setBreedName(String breedName) { this.breedName = breedName; }
        public ParentInfo getSire() { return sire; } public void setSire(ParentInfo sire) { this.sire = sire; }
        public ParentInfo getDam() { return dam; } public void setDam(ParentInfo dam) { this.dam = dam; }

        public static ParentInfoBuilder builder() { return new ParentInfoBuilder(); }
        public static class ParentInfoBuilder {
            private final ParentInfo info = new ParentInfo();
            public ParentInfoBuilder id(UUID id) { info.setId(id); return this; }
            public ParentInfoBuilder tagNumber(String tagNumber) { info.setTagNumber(tagNumber); return this; }
            public ParentInfoBuilder name(String name) { info.setName(name); return this; }
            public ParentInfoBuilder breedName(String breedName) { info.setBreedName(breedName); return this; }
            public ParentInfoBuilder sire(ParentInfo sire) { info.setSire(sire); return this; }
            public ParentInfoBuilder dam(ParentInfo dam) { info.setDam(dam); return this; }
            public ParentInfo build() { return info; }
        }
    }
}
