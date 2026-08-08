package com.pamir.ppfarmsbackend.identity.dto;

import com.pamir.ppfarmsbackend.identity.entity.ContactInquiry;
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
public class ContactInquiryResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String farmType;
    private Integer estimatedHerdSize;
    private String message;
    private String status;
    private OffsetDateTime createdAt;

    public static ContactInquiryResponse fromEntity(ContactInquiry inq) {
        if (inq == null) return null;
        return ContactInquiryResponse.builder()
                .id(inq.getId())
                .fullName(inq.getFullName())
                .email(inq.getEmail())
                .phone(inq.getPhone())
                .farmType(inq.getFarmType())
                .estimatedHerdSize(inq.getEstimatedHerdSize())
                .message(inq.getMessage())
                .status(inq.getStatus())
                .createdAt(inq.getCreatedAt())
                .build();
    }
}