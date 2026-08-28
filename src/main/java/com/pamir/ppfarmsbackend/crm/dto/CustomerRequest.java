package com.pamir.ppfarmsbackend.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;
    private String email;
    private String address;

    @Builder.Default
    private String customerType = "INDIVIDUAL"; // INDIVIDUAL, WHOLESALER, PROCESSOR, BUTCHER, COOPERATIVE

    private String preferredSpecies;
    private BigDecimal creditLimit;
    private String notes;
}
