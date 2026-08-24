package com.pamir.ppfarmsbackend.feed.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeedStockRequest {

    @NotBlank(message = "Feed name is required")
    private String feedName;

    @NotBlank(message = "Feed category is required (CONCENTRATE, GREEN_FODDER, DRY_FODDER, MINERAL_MIX, SILAGE)")
    private String feedCategory;

    @NotNull(message = "Quantity in Kg is required")
    @DecimalMin(value = "0.00", message = "Quantity cannot be negative")
    private BigDecimal quantityKg;

    @NotNull(message = "Cost per Kg is required")
    @DecimalMin(value = "0.00", message = "Cost cannot be negative")
    private BigDecimal costPerKg;

    private BigDecimal minThresholdKg = new BigDecimal("50.00");

    public String getFeedName() { return feedName; } public void setFeedName(String feedName) { this.feedName = feedName; }
    public String getFeedCategory() { return feedCategory; } public void setFeedCategory(String feedCategory) { this.feedCategory = feedCategory; }
    public BigDecimal getQuantityKg() { return quantityKg; } public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
    public BigDecimal getCostPerKg() { return costPerKg; } public void setCostPerKg(BigDecimal costPerKg) { this.costPerKg = costPerKg; }
    public BigDecimal getMinThresholdKg() { return minThresholdKg; } public void setMinThresholdKg(BigDecimal minThresholdKg) { this.minThresholdKg = minThresholdKg; }
}
