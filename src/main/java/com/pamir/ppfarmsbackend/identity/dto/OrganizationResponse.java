package com.pamir.ppfarmsbackend.identity.dto;

import com.pamir.ppfarmsbackend.identity.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
    private UUID id;
    private String name;
    private String phone;
    private String address;
    private String currency;
    private String status;
    private OffsetDateTime createdAt;

    public static OrganizationResponse fromEntity(Organization org) {
        if (org == null) return null;
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .phone(org.getPhone())
                .address(org.getAddress())
                .currency(org.getCurrency())
                .status(org.getStatus())
                .createdAt(org.getCreatedAt())
                .build();
    }
}