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
    private String organizationName;
    private String farmName;
    private String ownerName;
    private String organizationEmail;
    private String organizationPhone;
    private UUID subscriptionId;
    private UUID targetPlanId;
    private String planName;
    private String planType;
    private BigDecimal planPrice;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionRef;
    private String receiptImageUrl;
    private String status;
    private String rejectionReason;
    private UUID verifiedBy;
    private OffsetDateTime verifiedAt;
    private OffsetDateTime createdAt;
}
