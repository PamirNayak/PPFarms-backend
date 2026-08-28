package com.pamir.ppfarmsbackend.purchases.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseRequest {

    private String invoiceNumber;

    @NotBlank(message = "Vendor name is required")
    private String vendorName;

    private String vendorContact;

    private String paymentStatus = "PAID"; // PAID, PARTIAL, UNPAID

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    private String notes;

    @NotEmpty(message = "At least one purchase item is required")
    private List<PurchaseItemDto> items;

    public String getInvoiceNumber() { return invoiceNumber; } public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getVendorName() { return vendorName; } public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getVendorContact() { return vendorContact; } public void setVendorContact(String vendorContact) { this.vendorContact = vendorContact; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getPurchaseDate() { return purchaseDate; } public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public List<PurchaseItemDto> getItems() { return items; } public void setItems(List<PurchaseItemDto> items) { this.items = items; }
}
