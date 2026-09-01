package com.pamir.ppfarmsbackend.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentSubmitRequest {

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // UPI_QR, BANK_TRANSFER, CASH

    @NotBlank(message = "Transaction UTR / Reference number is required")
    private String transactionRef;

    @NotBlank(message = "Receipt image URL is required")
    private String receiptImageUrl;

    public UUID getPlanId() { return planId; } public void setPlanId(UUID planId) { this.planId = planId; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionRef() { return transactionRef; } public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getReceiptImageUrl() { return receiptImageUrl; } public void setReceiptImageUrl(String receiptImageUrl) { this.receiptImageUrl = receiptImageUrl; }
}
