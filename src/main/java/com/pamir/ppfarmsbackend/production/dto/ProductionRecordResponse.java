package com.pamir.ppfarmsbackend.production.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionRecordResponse {

    private UUID id;
    private UUID organizationId;
    private UUID animalId;
    private String animalTagNumber;
    private UUID shedPenId;
    private String shedPenName;
    private String productionType;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal fatPercentage;
    private BigDecimal snfPercentage;
    private UUID speciesId;
    private String speciesName;
    private LocalDate recordedDate;
    private String notes;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public String getAnimalTagNumber() { return animalTagNumber; } public void setAnimalTagNumber(String animalTagNumber) { this.animalTagNumber = animalTagNumber; }
    public UUID getSpeciesId() { return speciesId; } public void setSpeciesId(UUID speciesId) { this.speciesId = speciesId; }
    public String getSpeciesName() { return speciesName; } public void setSpeciesName(String speciesName) { this.speciesName = speciesName; }
    public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
    public String getShedPenName() { return shedPenName; } public void setShedPenName(String shedPenName) { this.shedPenName = shedPenName; }
    public String getProductionType() { return productionType; } public void setProductionType(String productionType) { this.productionType = productionType; }
    public BigDecimal getQuantity() { return quantity; } public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; } public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getFatPercentage() { return fatPercentage; } public void setFatPercentage(BigDecimal fatPercentage) { this.fatPercentage = fatPercentage; }
    public BigDecimal getSnfPercentage() { return snfPercentage; } public void setSnfPercentage(BigDecimal snfPercentage) { this.snfPercentage = snfPercentage; }
    public LocalDate getRecordedDate() { return recordedDate; } public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static ProductionRecordResponseBuilder builder() { return new ProductionRecordResponseBuilder(); }
    public static class ProductionRecordResponseBuilder {
        private final ProductionRecordResponse res = new ProductionRecordResponse();
        public ProductionRecordResponseBuilder id(UUID id) { res.setId(id); return this; }
        public ProductionRecordResponseBuilder organizationId(UUID organizationId) { res.setOrganizationId(organizationId); return this; }
        public ProductionRecordResponseBuilder animalId(UUID animalId) { res.setAnimalId(animalId); return this; }
        public ProductionRecordResponseBuilder animalTagNumber(String animalTagNumber) { res.setAnimalTagNumber(animalTagNumber); return this; }
        public ProductionRecordResponseBuilder speciesId(UUID speciesId) { res.setSpeciesId(speciesId); return this; }
        public ProductionRecordResponseBuilder speciesName(String speciesName) { res.setSpeciesName(speciesName); return this; }
        public ProductionRecordResponseBuilder shedPenId(UUID shedPenId) { res.setShedPenId(shedPenId); return this; }
        public ProductionRecordResponseBuilder shedPenName(String shedPenName) { res.setShedPenName(shedPenName); return this; }
        public ProductionRecordResponseBuilder productionType(String productionType) { res.setProductionType(productionType); return this; }
        public ProductionRecordResponseBuilder quantity(BigDecimal quantity) { res.setQuantity(quantity); return this; }
        public ProductionRecordResponseBuilder unit(String unit) { res.setUnit(unit); return this; }
        public ProductionRecordResponseBuilder fatPercentage(BigDecimal fatPercentage) { res.setFatPercentage(fatPercentage); return this; }
        public ProductionRecordResponseBuilder snfPercentage(BigDecimal snfPercentage) { res.setSnfPercentage(snfPercentage); return this; }
        public ProductionRecordResponseBuilder recordedDate(LocalDate recordedDate) { res.setRecordedDate(recordedDate); return this; }
        public ProductionRecordResponseBuilder notes(String notes) { res.setNotes(notes); return this; }
        public ProductionRecordResponseBuilder createdAt(OffsetDateTime createdAt) { res.setCreatedAt(createdAt); return this; }
        public ProductionRecordResponse build() { return res; }
    }
}
