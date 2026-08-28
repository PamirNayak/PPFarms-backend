package com.pamir.ppfarmsbackend.sales.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private UUID id;
    private UUID organizationId;
    private String invoiceNumber;
    private String buyerName;
    private String buyerContact;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDate saleDate;
    private String notes;
    private List<SaleItemDto> items;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getInvoiceNumber() { return invoiceNumber; } public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getBuyerName() { return buyerName; } public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerContact() { return buyerContact; } public void setBuyerContact(String buyerContact) { this.buyerContact = buyerContact; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getSaleDate() { return saleDate; } public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public List<SaleItemDto> getItems() { return items; } public void setItems(List<SaleItemDto> items) { this.items = items; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static SaleResponseBuilder builder() { return new SaleResponseBuilder(); }
    public static class SaleResponseBuilder {
        private final SaleResponse r = new SaleResponse();
        public SaleResponseBuilder id(UUID id) { r.setId(id); return this; }
        public SaleResponseBuilder organizationId(UUID organizationId) { r.setOrganizationId(organizationId); return this; }
        public SaleResponseBuilder invoiceNumber(String invoiceNumber) { r.setInvoiceNumber(invoiceNumber); return this; }
        public SaleResponseBuilder buyerName(String buyerName) { r.setBuyerName(buyerName); return this; }
        public SaleResponseBuilder buyerContact(String buyerContact) { r.setBuyerContact(buyerContact); return this; }
        public SaleResponseBuilder totalAmount(BigDecimal totalAmount) { r.setTotalAmount(totalAmount); return this; }
        public SaleResponseBuilder paymentStatus(String paymentStatus) { r.setPaymentStatus(paymentStatus); return this; }
        public SaleResponseBuilder saleDate(LocalDate saleDate) { r.setSaleDate(saleDate); return this; }
        public SaleResponseBuilder notes(String notes) { r.setNotes(notes); return this; }
        public SaleResponseBuilder items(List<SaleItemDto> items) { r.setItems(items); return this; }
        public SaleResponseBuilder createdAt(OffsetDateTime createdAt) { r.setCreatedAt(createdAt); return this; }
        public SaleResponse build() { return r; }
    }
}
