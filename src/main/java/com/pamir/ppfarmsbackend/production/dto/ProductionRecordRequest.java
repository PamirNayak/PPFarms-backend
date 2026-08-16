package com.pamir.ppfarmsbackend.production.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProductionRecordRequest {

    private UUID animalId;
    private UUID shedPenId;

    @NotBlank(message = "Production type is required (e.g. MILK_MORNING, MILK_EVENING, WOOL_FIBER)")
    private String productionType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private String unit = "LITERS";
    private BigDecimal fatPercentage;
    private BigDecimal snfPercentage;

    @NotNull(message = "Recorded date is required")
    private LocalDate recordedDate;

    private String notes;

    public UUID getAnimalId() { return animalId; } public void setAnimalId(UUID animalId) { this.animalId = animalId; }
    public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
    public String getProductionType() { return productionType; } public void setProductionType(String productionType) { this.productionType = productionType; }
    public BigDecimal getQuantity() { return quantity; } public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; } public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getFatPercentage() { return fatPercentage; } public void setFatPercentage(BigDecimal fatPercentage) { this.fatPercentage = fatPercentage; }
    public BigDecimal getSnfPercentage() { return snfPercentage; } public void setSnfPercentage(BigDecimal snfPercentage) { this.snfPercentage = snfPercentage; }
    public LocalDate getRecordedDate() { return recordedDate; } public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
}
