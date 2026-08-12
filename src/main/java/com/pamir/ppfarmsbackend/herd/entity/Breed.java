package com.pamir.ppfarmsbackend.herd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "breeds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public Species getSpecies() { return species; } public void setSpecies(Species species) { this.species = species; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }

    public static BreedBuilder builder() { return new BreedBuilder(); }
    public static class BreedBuilder {
        private final Breed b = new Breed();
        public BreedBuilder id(UUID id) { b.setId(id); return this; }
        public BreedBuilder organizationId(UUID organizationId) { b.setOrganizationId(organizationId); return this; }
        public BreedBuilder species(Species species) { b.setSpecies(species); return this; }
        public BreedBuilder name(String name) { b.setName(name); return this; }
        public BreedBuilder description(String description) { b.setDescription(description); return this; }
        public Breed build() { return b; }
    }
}
