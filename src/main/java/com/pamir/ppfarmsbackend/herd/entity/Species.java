package com.pamir.ppfarmsbackend.herd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "species")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "gestation_days", nullable = false)
    private Integer gestationDays;

    @Column(name = "heat_cycle_days", nullable = false)
    private Integer heatCycleDays;

    @Column(name = "supports_milking", nullable = false)
    @Builder.Default
    private Boolean supportsMilking = false;

    @Column(name = "supports_shearing", nullable = false)
    @Builder.Default
    private Boolean supportsShearing = false;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Integer getGestationDays() { return gestationDays; } public void setGestationDays(Integer gestationDays) { this.gestationDays = gestationDays; }
    public Integer getHeatCycleDays() { return heatCycleDays; } public void setHeatCycleDays(Integer heatCycleDays) { this.heatCycleDays = heatCycleDays; }
    public Boolean getSupportsMilking() { return supportsMilking; } public void setSupportsMilking(Boolean supportsMilking) { this.supportsMilking = supportsMilking; }
    public Boolean getSupportsShearing() { return supportsShearing; } public void setSupportsShearing(Boolean supportsShearing) { this.supportsShearing = supportsShearing; }

    public static SpeciesBuilder builder() { return new SpeciesBuilder(); }
    public static class SpeciesBuilder {
        private final Species s = new Species();
        public SpeciesBuilder id(UUID id) { s.setId(id); return this; }
        public SpeciesBuilder name(String name) { s.setName(name); return this; }
        public SpeciesBuilder gestationDays(Integer gestationDays) { s.setGestationDays(gestationDays); return this; }
        public SpeciesBuilder heatCycleDays(Integer heatCycleDays) { s.setHeatCycleDays(heatCycleDays); return this; }
        public SpeciesBuilder supportsMilking(Boolean supportsMilking) { s.setSupportsMilking(supportsMilking); return this; }
        public SpeciesBuilder supportsShearing(Boolean supportsShearing) { s.setSupportsShearing(supportsShearing); return this; }
        public Species build() { return s; }
    }
}
