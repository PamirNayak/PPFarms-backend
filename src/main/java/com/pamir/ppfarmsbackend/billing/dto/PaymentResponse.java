package com.pamir.ppfarmsbackend.billing.dto;

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
public class PaymentResponse {

    private UUID id;
    private UUID organizationId;
    private UUID subscriptionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionRef;
    private String receiptImageUrl;
    private String status;
    private String rejectionReason;
    private UUID verifiedBy;
    private OffsetDateTime verifiedAt;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public UUID getSubscriptionId() { return subscriptionId; } public void setSubscriptionId(UUID subscriptionId) { this.subscriptionId = subscriptionId; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionRef() { return transactionRef; } public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getReceiptImageUrl() { return receiptImageUrl; } public void setReceiptImageUrl(String receiptImageUrl) { this.receiptImageUrl = receiptImageUrl; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; } public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public UUID getVerifiedBy() { return verifiedBy; } public void setVerifiedBy(UUID verifiedBy) { this.verifiedBy = verifiedBy; }
    public OffsetDateTime getVerifiedAt() { return verifiedAt; } public void setVerifiedAt(OffsetDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static PaymentResponseBuilder builder() { return new PaymentResponseBuilder(); }
    public static class PaymentResponseBuilder {
        private final PaymentResponse res = new PaymentResponse();
        public PaymentResponseBuilder id(UUID id) { res.setId(id); return this; }
        public PaymentResponseBuilder organizationId(UUID organizationId) { res.setOrganizationId(organizationId); return this; }
        public PaymentResponseBuilder subscriptionId(UUID subscriptionId) { res.setSubscriptionId(subscriptionId); return this; }
        public PaymentResponseBuilder amount(BigDecimal amount) { res.setAmount(amount); return this; }
        public PaymentResponseBuilder paymentMethod(String paymentMethod) { res.setPaymentMethod(paymentMethod); return this; }
        public PaymentResponseBuilder transactionRef(String transactionRef) { res.setTransactionRef(transactionRef); return this; }
        public PaymentResponseBuilder receiptImageUrl(String receiptImageUrl) { res.setReceiptImageUrl(receiptImageUrl); return this; }
        public PaymentResponseBuilder status(String status) { res.setStatus(status); return this; }
        public PaymentResponseBuilder rejectionReason(String rejectionReason) { res.setRejectionReason(rejectionReason); return this; }
        public PaymentResponseBuilder verifiedBy(UUID verifiedBy) { res.setVerifiedBy(verifiedBy); return this; }
        public PaymentResponseBuilder verifiedAt(OffsetDateTime verifiedAt) { res.setVerifiedAt(verifiedAt); return this; }
        public PaymentResponseBuilder createdAt(OffsetDateTime createdAt) { res.setCreatedAt(createdAt); return this; }
        public PaymentResponse build() { return res; }
    }
}
