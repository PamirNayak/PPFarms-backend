package com.pamir.ppfarmsbackend.feed.dto;

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
public class FeedLogResponse {

    private UUID id;
    private UUID organizationId;
    private UUID feedInventoryId;
    private String feedName;
    private UUID shedPenId;
    private String shedPenName;
    private BigDecimal quantityConsumedKg;
    private BigDecimal totalCost;
    private LocalDate consumedDate;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getFeedInventoryId() { return feedInventoryId; } public void setFeedInventoryId(UUID feedInventoryId) { this.feedInventoryId = feedInventoryId; }
    public String getFeedName() { return feedName; } public void setFeedName(String feedName) { this.feedName = feedName; }
    public UUID getShedPenId() { return shedPenId; } public void setShedPenId(UUID shedPenId) { this.shedPenId = shedPenId; }
    public String getShedPenName() { return shedPenName; } public void setShedPenName(String shedPenName) { this.shedPenName = shedPenName; }
    public BigDecimal getQuantityConsumedKg() { return quantityConsumedKg; } public void setQuantityConsumedKg(BigDecimal quantityConsumedKg) { this.quantityConsumedKg = quantityConsumedKg; }
    public BigDecimal getTotalCost() { return totalCost; } public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public LocalDate getConsumedDate() { return consumedDate; } public void setConsumedDate(LocalDate consumedDate) { this.consumedDate = consumedDate; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static FeedLogResponseBuilder builder() { return new FeedLogResponseBuilder(); }
    public static class FeedLogResponseBuilder {
        private final FeedLogResponse res = new FeedLogResponse();
        public FeedLogResponseBuilder id(UUID id) { res.setId(id); return this; }
        public FeedLogResponseBuilder organizationId(UUID organizationId) { res.setOrganizationId(organizationId); return this; }
        public FeedLogResponseBuilder feedInventoryId(UUID feedInventoryId) { res.setFeedInventoryId(feedInventoryId); return this; }
        public FeedLogResponseBuilder feedName(String feedName) { res.setFeedName(feedName); return this; }
        public FeedLogResponseBuilder shedPenId(UUID shedPenId) { res.setShedPenId(shedPenId); return this; }
        public FeedLogResponseBuilder shedPenName(String shedPenName) { res.setShedPenName(shedPenName); return this; }
        public FeedLogResponseBuilder quantityConsumedKg(BigDecimal quantityConsumedKg) { res.setQuantityConsumedKg(quantityConsumedKg); return this; }
        public FeedLogResponseBuilder totalCost(BigDecimal totalCost) { res.setTotalCost(totalCost); return this; }
        public FeedLogResponseBuilder consumedDate(LocalDate consumedDate) { res.setConsumedDate(consumedDate); return this; }
        public FeedLogResponseBuilder createdAt(OffsetDateTime createdAt) { res.setCreatedAt(createdAt); return this; }
        public FeedLogResponse build() { return res; }
    }
}
