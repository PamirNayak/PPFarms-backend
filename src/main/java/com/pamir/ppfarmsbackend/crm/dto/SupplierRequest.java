package com.pamir.ppfarmsbackend.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String taxId;
    private String paymentTerms;
    private BigDecimal openingBalance;
    private Integer rating;
    private String notes;
}
