package com.pamir.ppfarmsbackend.feed.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class FeedConsumptionRequest {

    @NotNull(message = "Feed inventory ID is required")
    private UUID feedInventoryId;

    private UUID shedPenId;

    @NotNull(message = "Quantity consumed (Kg) is required")
    @DecimalMin(value = "0.01", message = "Quantity consumed must be greater than 0")
    private BigDecimal quantityConsumedKg;

    @NotNull(message = "Consumption date is required")
    private LocalDate consumedDate;

    public UUID getFeedInventoryId() { return feedInventoryId; } public void setFeedInventoryId(UUID feedInventoryId) { this.feedInventoryId = feedInventoryId; }
    public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
    public BigDecimal getQuantityConsumedKg() { return quantityConsumedKg; } public void setQuantityConsumedKg(BigDecimal quantityConsumedKg) { this.quantityConsumedKg = quantityConsumedKg; }
    public LocalDate getConsumedDate() { return consumedDate; } public void setConsumedDate(LocalDate consumedDate) { this.consumedDate = consumedDate; }
}
