package com.pamir.ppfarmsbackend.purchases.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {

    private UUID id;
    private UUID organizationId;
    private String invoiceNumber;
    private String vendorName;
    private String vendorContact;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDate purchaseDate;
    private String notes;
    private List<PurchaseItemDto> items;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getInvoiceNumber() { return invoiceNumber; } public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getVendorName() { return vendorName; } public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getVendorContact() { return vendorContact; } public void setVendorContact(String vendorContact) { this.vendorContact = vendorContact; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getPurchaseDate() { return purchaseDate; } public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public List<PurchaseItemDto> getItems() { return items; } public void setItems(List<PurchaseItemDto> items) { this.items = items; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static PurchaseResponseBuilder builder() { return new PurchaseResponseBuilder(); }
    public static class PurchaseResponseBuilder {
        private final PurchaseResponse res = new PurchaseResponse();
        public PurchaseResponseBuilder id(UUID id) { res.setId(id); return this; }
        public PurchaseResponseBuilder organizationId(UUID organizationId) { res.setOrganizationId(organizationId); return this; }
        public PurchaseResponseBuilder invoiceNumber(String invoiceNumber) { res.setInvoiceNumber(invoiceNumber); return this; }
        public PurchaseResponseBuilder vendorName(String vendorName) { res.setVendorName(vendorName); return this; }
        public PurchaseResponseBuilder vendorContact(String vendorContact) { res.setVendorContact(vendorContact); return this; }
        public PurchaseResponseBuilder totalAmount(BigDecimal totalAmount) { res.setTotalAmount(totalAmount); return this; }
        public PurchaseResponseBuilder paymentStatus(String paymentStatus) { res.setPaymentStatus(paymentStatus); return this; }
        public PurchaseResponseBuilder purchaseDate(LocalDate purchaseDate) { res.setPurchaseDate(purchaseDate); return this; }
        public PurchaseResponseBuilder notes(String notes) { res.setNotes(notes); return this; }
        public PurchaseResponseBuilder items(List<PurchaseItemDto> items) { res.setItems(items); return this; }
        public PurchaseResponseBuilder createdAt(OffsetDateTime createdAt) { res.setCreatedAt(createdAt); return this; }
        public PurchaseResponse build() { return res; }
    }
}
