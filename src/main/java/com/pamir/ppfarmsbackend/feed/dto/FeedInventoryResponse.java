package com.pamir.ppfarmsbackend.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedInventoryResponse {

    private UUID id;
    private UUID organizationId;
    private String feedName;
    private String feedCategory;
    private BigDecimal quantityKg;
    private BigDecimal costPerKg;
    private BigDecimal minThresholdKg;
    private Boolean isLowStock;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getFeedName() { return feedName; } public void setFeedName(String feedName) { this.feedName = feedName; }
    public String getFeedCategory() { return feedCategory; } public void setFeedCategory(String feedCategory) { this.feedCategory = feedCategory; }
    public BigDecimal getQuantityKg() { return quantityKg; } public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
    public BigDecimal getCostPerKg() { return costPerKg; } public void setCostPerKg(BigDecimal costPerKg) { this.costPerKg = costPerKg; }
    public BigDecimal getMinThresholdKg() { return minThresholdKg; } public void setMinThresholdKg(BigDecimal minThresholdKg) { this.minThresholdKg = minThresholdKg; }
    public Boolean getIsLowStock() { return isLowStock; } public void setIsLowStock(Boolean isLowStock) { this.isLowStock = isLowStock; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static FeedInventoryResponseBuilder builder() { return new FeedInventoryResponseBuilder(); }
    public static class FeedInventoryResponseBuilder {
        private final FeedInventoryResponse res = new FeedInventoryResponse();
        public FeedInventoryResponseBuilder id(UUID id) { res.setId(id); return this; }
        public FeedInventoryResponseBuilder organizationId(UUID organizationId) { res.setOrganizationId(organizationId); return this; }
        public FeedInventoryResponseBuilder feedName(String feedName) { res.setFeedName(feedName); return this; }
        public FeedInventoryResponseBuilder feedCategory(String feedCategory) { res.setFeedCategory(feedCategory); return this; }
        public FeedInventoryResponseBuilder quantityKg(BigDecimal quantityKg) { res.setQuantityKg(quantityKg); return this; }
        public FeedInventoryResponseBuilder costPerKg(BigDecimal costPerKg) { res.setCostPerKg(costPerKg); return this; }
        public FeedInventoryResponseBuilder minThresholdKg(BigDecimal minThresholdKg) { res.setMinThresholdKg(minThresholdKg); return this; }
        public FeedInventoryResponseBuilder isLowStock(Boolean isLowStock) { res.setIsLowStock(isLowStock); return this; }
        public FeedInventoryResponseBuilder createdAt(OffsetDateTime createdAt) { res.setCreatedAt(createdAt); return this; }
        public FeedInventoryResponse build() { return res; }
    }
}
