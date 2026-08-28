package com.pamir.ppfarmsbackend.accounting.dto;

import com.pamir.ppfarmsbackend.accounting.enums.IncomeCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse {

    private UUID id;
    private UUID organizationId;
    private IncomeCategory category;
    private String description;
    private BigDecimal amount;
    private LocalDate incomeDate;
    private String sourceName;
    private String referenceNumber;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public IncomeCategory getCategory() { return category; } public void setCategory(IncomeCategory category) { this.category = category; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getIncomeDate() { return incomeDate; } public void setIncomeDate(LocalDate incomeDate) { this.incomeDate = incomeDate; }
    public String getSourceName() { return sourceName; } public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getReferenceNumber() { return referenceNumber; } public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static IncomeResponseBuilder builder() { return new IncomeResponseBuilder(); }
    public static class IncomeResponseBuilder {
        private final IncomeResponse r = new IncomeResponse();
        public IncomeResponseBuilder id(UUID id) { r.setId(id); return this; }
        public IncomeResponseBuilder organizationId(UUID organizationId) { r.setOrganizationId(organizationId); return this; }
        public IncomeResponseBuilder category(IncomeCategory category) { r.setCategory(category); return this; }
        public IncomeResponseBuilder description(String description) { r.setDescription(description); return this; }
        public IncomeResponseBuilder amount(BigDecimal amount) { r.setAmount(amount); return this; }
        public IncomeResponseBuilder incomeDate(LocalDate incomeDate) { r.setIncomeDate(incomeDate); return this; }
        public IncomeResponseBuilder sourceName(String sourceName) { r.setSourceName(sourceName); return this; }
        public IncomeResponseBuilder referenceNumber(String referenceNumber) { r.setReferenceNumber(referenceNumber); return this; }
        public IncomeResponseBuilder createdAt(OffsetDateTime createdAt) { r.setCreatedAt(createdAt); return this; }
        public IncomeResponse build() { return r; }
    }
}
