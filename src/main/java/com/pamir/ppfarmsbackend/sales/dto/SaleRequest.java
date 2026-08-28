package com.pamir.ppfarmsbackend.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class SaleRequest {

    @NotBlank(message = "Buyer name is required")
    private String buyerName;

    private String buyerContact;

    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;

    private String paymentStatus = "PAID"; // PAID, PARTIAL, UNPAID

    private String notes;

    @NotEmpty(message = "Sale must contain at least one line item")
    @Valid
    private List<SaleItemDto> items;

    public String getBuyerName() { return buyerName; } public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerContact() { return buyerContact; } public void setBuyerContact(String buyerContact) { this.buyerContact = buyerContact; }
    public LocalDate getSaleDate() { return saleDate; } public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public String getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public List<SaleItemDto> getItems() { return items; } public void setItems(List<SaleItemDto> items) { this.items = items; }
}
