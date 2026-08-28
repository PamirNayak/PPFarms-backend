package com.pamir.ppfarmsbackend.accounting.dto;

import com.pamir.ppfarmsbackend.accounting.enums.IncomeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeRequest {

    @NotNull(message = "Income category is required")
    private IncomeCategory category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

    private String sourceName;
    private String referenceNumber;

    public IncomeCategory getCategory() { return category; } public void setCategory(IncomeCategory category) { this.category = category; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getIncomeDate() { return incomeDate; } public void setIncomeDate(LocalDate incomeDate) { this.incomeDate = incomeDate; }
    public String getSourceName() { return sourceName; } public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getReferenceNumber() { return referenceNumber; } public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public static IncomeRequestBuilder builder() { return new IncomeRequestBuilder(); }
    public static class IncomeRequestBuilder {
        private final IncomeRequest r = new IncomeRequest();
        public IncomeRequestBuilder category(IncomeCategory category) { r.setCategory(category); return this; }
        public IncomeRequestBuilder description(String description) { r.setDescription(description); return this; }
        public IncomeRequestBuilder amount(BigDecimal amount) { r.setAmount(amount); return this; }
        public IncomeRequestBuilder incomeDate(LocalDate incomeDate) { r.setIncomeDate(incomeDate); return this; }
        public IncomeRequestBuilder sourceName(String sourceName) { r.setSourceName(sourceName); return this; }
        public IncomeRequestBuilder referenceNumber(String referenceNumber) { r.setReferenceNumber(referenceNumber); return this; }
        public IncomeRequest build() { return r; }
    }
}
