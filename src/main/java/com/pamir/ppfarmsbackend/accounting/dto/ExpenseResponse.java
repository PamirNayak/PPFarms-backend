package com.pamir.ppfarmsbackend.accounting.dto;

import com.pamir.ppfarmsbackend.accounting.enums.ExpenseCategory;
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
public class ExpenseResponse {

    private UUID id;
    private UUID organizationId;
    private ExpenseCategory category;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String vendorName;
    private String receiptNumber;
    private String paymentMethod;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public ExpenseCategory getCategory() { return category; } public void setCategory(ExpenseCategory category) { this.category = category; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getExpenseDate() { return expenseDate; } public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getVendorName() { return vendorName; } public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getReceiptNumber() { return receiptNumber; } public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static ExpenseResponseBuilder builder() { return new ExpenseResponseBuilder(); }
    public static class ExpenseResponseBuilder {
        private final ExpenseResponse r = new ExpenseResponse();
        public ExpenseResponseBuilder id(UUID id) { r.setId(id); return this; }
        public ExpenseResponseBuilder organizationId(UUID organizationId) { r.setOrganizationId(organizationId); return this; }
        public ExpenseResponseBuilder category(ExpenseCategory category) { r.setCategory(category); return this; }
        public ExpenseResponseBuilder description(String description) { r.setDescription(description); return this; }
        public ExpenseResponseBuilder amount(BigDecimal amount) { r.setAmount(amount); return this; }
        public ExpenseResponseBuilder expenseDate(LocalDate expenseDate) { r.setExpenseDate(expenseDate); return this; }
        public ExpenseResponseBuilder vendorName(String vendorName) { r.setVendorName(vendorName); return this; }
        public ExpenseResponseBuilder receiptNumber(String receiptNumber) { r.setReceiptNumber(receiptNumber); return this; }
        public ExpenseResponseBuilder paymentMethod(String paymentMethod) { r.setPaymentMethod(paymentMethod); return this; }
        public ExpenseResponseBuilder createdAt(OffsetDateTime createdAt) { r.setCreatedAt(createdAt); return this; }
        public ExpenseResponse build() { return r; }
    }
}
