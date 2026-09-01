package com.pamir.ppfarmsbackend.documents.dto;

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
public class DocumentResponse {

    private UUID id;
    private UUID organizationId;
    private String entityType;
    private UUID entityId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private UUID uploadedByUserId;
    private String uploadedByUserName;
    private String notes;
    private OffsetDateTime createdAt;
}
