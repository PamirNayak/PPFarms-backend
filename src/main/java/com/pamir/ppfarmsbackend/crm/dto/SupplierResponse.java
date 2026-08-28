package com.pamir.ppfarmsbackend.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String taxId;
    private String paymentTerms;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private Integer rating;
    private String notes;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
