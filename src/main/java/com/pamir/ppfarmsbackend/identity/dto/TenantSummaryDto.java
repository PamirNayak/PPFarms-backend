package com.pamir.ppfarmsbackend.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSummaryDto {
    private UUID id;
    private String farmName;
    private String email;
    private String phone;
    private String address;
    private String currency;
    private String status; // ACTIVE, SUSPENDED

    private UUID ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

    private UUID planId;
    private String planName;
    private String planType; // FREE, MONTHLY, ANNUAL
    private String subscriptionStatus; // TRIAL, ACTIVE, EXPIRED, UNCLAIMED, PENDING_APPROVAL
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private long daysRemaining;

    private long totalAnimals;
    private long totalStaff;
    private OffsetDateTime createdAt;
}
