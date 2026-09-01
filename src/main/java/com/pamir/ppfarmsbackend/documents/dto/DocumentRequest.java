package com.pamir.ppfarmsbackend.documents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {

    @NotBlank(message = "Entity type is required")
    private String entityType; // ANIMAL, HEALTH, PURCHASE, SALE, FARM, VACCINE

    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private String fileType;
    private Long fileSizeBytes;
    private String notes;
}
